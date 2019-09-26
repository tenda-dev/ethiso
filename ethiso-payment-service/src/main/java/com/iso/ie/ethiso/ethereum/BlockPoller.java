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
package com.iso.ie.ethiso.ethereum;

import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iso.ie.ethiso.ethereum.rpc.BlockInfo;
import com.iso.ie.ethiso.ethereum.rpc.EthereumRpc;

public class BlockPoller extends TimerTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlockPoller.class);

	// private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final EthereumRpc rpc;
	private final PendingEvents pendingEvents;
	private final String blockFilterHash;

	BlockPoller(final EthereumRpc rpc, final PendingEvents pendingEvents) {
		this.rpc = rpc;
		this.pendingEvents = pendingEvents;

		blockFilterHash = rpc.eth_newBlockFilter();
	}

	@Override
	public void run() {
		try {
			final List<Object> list = rpc.eth_getFilterChanges(blockFilterHash);

			if (!list.isEmpty()) {
				LOGGER.debug("Found [" + list.size() + "] blocks " + list);
				
				for (final Object hash : list) {
					final BlockInfo blockInfo = rpc.eth_getBlockByHash(String.valueOf(hash), false);

					pendingEvents.newBlock(rpc, blockInfo);
				}
			}
		} catch (final RuntimeException e) {
			LOGGER.warn("Failed to poll event", e);
		}
	}

	public void addEventListener(final List<String> topics, final FunctionEncoding functionEncoding, final Consumer<Object[]> handler) {
		pendingEvents.addEventListener(topics, functionEncoding, handler);
	}
}