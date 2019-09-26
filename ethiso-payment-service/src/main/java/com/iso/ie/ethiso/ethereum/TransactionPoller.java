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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iso.ie.ethiso.ethereum.rpc.EthereumRpc;
import com.iso.ie.ethiso.ethereum.rpc.Transaction;
import com.iso.ie.ethiso.ethereum.rpc.TransactionReceipt;
import com.iso.ie.jsonrpc.JsonRpcException;

class TransactionPoller extends TimerTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionPoller.class);

	private final Map<String, TransactionAndReceipt> pendingTransactions = new ConcurrentHashMap<>();
	private final EthereumRpc rpc;

	TransactionPoller(final EthereumRpc rpc) {
		this.rpc = rpc;
	}

	public void addPendingTransaction(final String transactionHash,
			final TransactionAndReceipt transactionAndReceipt) {
		LOGGER.debug("tk=pta,tid="+transactionHash);
		pendingTransactions.put(transactionHash, transactionAndReceipt);
	}

	@Override
	public void run() {
		try {
			final List<String> completedTransactions = new ArrayList<>();

			for (final Entry<String, TransactionAndReceipt> pendingTransaction : pendingTransactions.entrySet()) {
				final TransactionReceipt transactionReceipt = rpc
						.eth_getTransactionReceipt(pendingTransaction.getKey());

				if (transactionReceipt != null) {
					completedTransactions.add(pendingTransaction.getKey());

					LOGGER.debug("gs=" + pendingTransaction.getValue().getTransaction().getGas() + ",gu="
							+ transactionReceipt.getGasUsed() + ",tid="+pendingTransaction.getKey());

					pendingTransaction.getValue().complete(transactionReceipt);
				}
			}

			for (final String completedTransaction : completedTransactions) {
				pendingTransactions.remove(completedTransaction);
			}

			if (!pendingTransactions.isEmpty()) {
				LOGGER.debug("ptc=" + pendingTransactions.size());
			}

		} catch (final RuntimeException e) {
			LOGGER.warn("Failed to poll transactions [" + e + "]");
		}
	}

	static class TransactionAndReceipt {
		private static final int MAX_RETRIES = 2;
		
		private final Transaction transaction;
		private final CompletableFuture<TransactionReceipt> futureTransactionReceipt;
		private final List<Exception> failures = new ArrayList<>(0);
		
		public TransactionAndReceipt(final Transaction transaction) {
			this.transaction = transaction;
			this.futureTransactionReceipt = new CompletableFuture<>();
		}

		public Transaction getTransaction() {
			return transaction;
		}

		public CompletionStage<TransactionReceipt> getFutureTransactionReceipt() {
			return futureTransactionReceipt;
		}

		public void complete(final TransactionReceipt transactionReceipt) {
			futureTransactionReceipt.complete(transactionReceipt);
		}

		/**
		 * @return true if we should retry the transaction
		 */
		public boolean failed(final JsonRpcException e) {
			failures.add(e);
			
			return failures.size() <= MAX_RETRIES;
		}
	}
}