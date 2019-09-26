/*******************************************************************************
 * Copyright (c) 2016 Royal Bank of Scotland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.std.ie.ethiso.ethereum;

import static java.math.BigInteger.ZERO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.ie.ethiso.ethereum.rpc.BlockInfo;
import com.std.ie.ethiso.ethereum.rpc.EthereumRpc;
import com.std.ie.ethiso.ethereum.rpc.LogEntry;
import com.std.ie.ethiso.ethereum.rpc.Quantity;
import com.std.ie.ethiso.ethereum.rpc.TransactionReceipt;

public class PendingEvents {

	private static final Logger LOGGER = LoggerFactory.getLogger(PendingEvents.class);

	private static final int DEFAULT_NUMBER_OF_BLOCKS_TO_CONFIRM = 5;
	private static final int PREFIX_LEN = 2;
	private static final int SHORT_HASH_LEN = 10;

	private final int blockConfirmationCount;

	private final Map<String, List<FunctionEncodingAndHandler>> eventListeners = new HashMap<>();
	private final SortedMap<Integer, Set<String>> blockDepth = new TreeMap<>();
	private final Map<String, BlockInfo> blockInfoMap = new HashMap<>();

	private final Lock lock = new ReentrantLock();

	public PendingEvents() {
		this(DEFAULT_NUMBER_OF_BLOCKS_TO_CONFIRM);
	}

	public PendingEvents(final int blocksConfirmationCount) {
		if (blocksConfirmationCount < 0) {
			throw new IllegalArgumentException("[numberOfBlocksToConfirm=" + blocksConfirmationCount + "]");
		}

		this.blockConfirmationCount = blocksConfirmationCount;
	}

	public void newBlock(final EthereumRpc rpc, final BlockInfo blockInfo) {
		lock.lock();

		try {
			importBlocks(rpc, blockInfo);

			final Integer removalBoundry = Integer
					.valueOf(blockDepth.lastKey().intValue() - (blockConfirmationCount - 1));

			final SortedMap<Integer, Set<String>> blocksToRemove = blockDepth.headMap(removalBoundry);

			if (!blocksToRemove.isEmpty()) {
				final Set<String> inChain = new HashSet<>();

				for (final String deepestBlockHash : blockDepth.get(blockDepth.lastKey())) {

					String blockHash = deepestBlockHash;

					while (blockInfoMap.containsKey(blockHash)) {
						inChain.add(blockHash);

						blockHash = blockInfoMap.get(blockHash).getParentHash();
					}
				}

				for (final Map.Entry<Integer, Set<String>> blockHashes : blocksToRemove.entrySet()) {
					for (final String blockHash : blockHashes.getValue()) {
						final BlockInfo blockToRemove = blockInfoMap.remove(blockHash);

						if (inChain.contains(blockHash)) {
							LOGGER.debug("tk=bf, bn=" + blockHashes.getKey() + ",bh=" + blockHash);
							for (final Object transactionHash : blockToRemove.getTransactions()) {
								final TransactionReceipt transactionReceipt = rpc
										.eth_getTransactionReceipt(String.valueOf(transactionHash));

								LOGGER.debug("tk=tf, bn=" + blockHashes.getKey() + ",bh=" + blockHash + ",tid=" + transactionHash);

								for (final LogEntry logEntry : transactionReceipt.getLogs()) {
									final List<FunctionEncodingAndHandler> handlers = eventListeners.get(logEntry.getTopics().get(0));
									
									if (handlers != null) {										
										for (final FunctionEncodingAndHandler handler : handlers) {
											handler.fire(logEntry);
										}
									}							
								}
							}
						} else {
							LOGGER.info("tk=prune, bn=" + blockHashes.getKey() + ",bh=" + blockHash);
						}
					}
				}

				blocksToRemove.clear();
			}
		} finally {
			lock.unlock();
		}
	}

	private void importBlocks(final EthereumRpc rpc, final BlockInfo blockInfo) {
		BlockInfo currentBlockInfo = blockInfo;

		final List<String> blocks = new LinkedList<>();

		blocks.add(shortHash(currentBlockInfo.getHash()));

		boolean found = false;

		int depth = 0;

		while (!found) {
			final Integer blockNumber = Integer.valueOf(currentBlockInfo.getNumber().intValue());

			blockDepth.putIfAbsent(blockNumber, new HashSet<>());
			blockDepth.get(blockNumber).add(currentBlockInfo.getHash());

			blockInfoMap.put(currentBlockInfo.getHash(), currentBlockInfo);

			final String parentHash = currentBlockInfo.getParentHash();

			blocks.add(0, shortHash(parentHash));

			if (new Quantity(parentHash).equals(ZERO) || blockInfoMap.containsKey(parentHash)
					|| depth > blockConfirmationCount + DEFAULT_NUMBER_OF_BLOCKS_TO_CONFIRM) {
				found = true;
			} else {
				currentBlockInfo = rpc.eth_getBlockByHash(parentHash, false);
				depth++;
			}
		}

		LOGGER.info("Blocks: " + String.join(" <- ", blocks));
	}

	private static final String shortHash(final String hash) {
		return hash.substring(PREFIX_LEN, SHORT_HASH_LEN);
	}

	public void addEventListener(final List<String> topics, final FunctionEncoding functionEncoding,
			final Consumer<Object[]> handler) {
		lock.lock();

		try {
			eventListeners.putIfAbsent(topics.get(0), new ArrayList<>());

			eventListeners.get(topics.get(0)).add(new FunctionEncodingAndHandler(topics, functionEncoding, handler));
		} finally {
			lock.unlock();
		}
	}

	private static class FunctionEncodingAndHandler {
		private final List<String> topics;
		private final FunctionEncoding functionEncoding;
		private final Consumer<Object[]> handler;

		public FunctionEncodingAndHandler(final List<String> topics, final FunctionEncoding functionEncoding,
				final Consumer<Object[]> handler) {
			this.topics = topics;
			this.functionEncoding = functionEncoding;
			this.handler = handler;
		}

		public void fire(final LogEntry logEntry) {
			boolean match = true;
			
			for (int idx = 1; idx < topics.size(); idx++) {
				match &= topics.get(idx).equals(logEntry.getTopics().get(idx)); 
			}
			
			if (match) {
				handler.accept(functionEncoding.decodeEvent(logEntry));
			}
		}
	}
}