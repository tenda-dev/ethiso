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
package com.rbs.ie.emerald.ethereum;

import static com.rbs.ie.emerald.ethereum.Utils.DEFAULT_TIMEOUT;
import static com.rbs.ie.emerald.ethereum.Utils.waitFor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rbs.ie.emerald.ethereum.TransactionPoller.TransactionAndReceipt;
import com.rbs.ie.emerald.ethereum.rpc.Address;
import com.rbs.ie.emerald.ethereum.rpc.CompilationResults;
import com.rbs.ie.emerald.ethereum.rpc.EthereumRpc;
import com.rbs.ie.emerald.ethereum.rpc.Quantity;
import com.rbs.ie.emerald.ethereum.rpc.Transaction;
import com.rbs.ie.emerald.ethereum.rpc.TransactionReceipt;
import com.rbs.ie.jsonrpc.JsonRpcException;
import com.rbs.ie.jsonrpc.JsonRpcFactory;
import com.rbs.ie.jsonrpc.JsonRpcTransport;
import com.rbs.ie.jsonrpc.http.HttpJsonRpcTransport;

public class Ethereum implements AutoCloseable {

	private static final Logger LOGGER = LoggerFactory.getLogger(Ethereum.class);

	private static final String ZERO_VALUE = "0x0";
	private static final int TIMEOUT_SECONDS = 20;
	private static final int DEFAULT_THREADS = 1;
	private static final int POLL_FREQUENCY_MILLIS = 1000;
	private static final String NEW_CONTRACT_GAS = "0x1000000";
	private static final String MAIN_GAS = "0x80000";

	public static final InetSocketAddress DEFAULT_SOCKET_ADDRESS = InetSocketAddress.createUnresolved("localhost",
			8545);
	private static final int DEFAULT_BLOCK_CONFIRMATION_COUNT = 5;
	private static final String DEFAULT_ACCOUNT = null;

	private static final int TRANSACTIONS_PER_SECOND = 20;
	private static final long TRANSACTION_DELAY = 1000 / TRANSACTIONS_PER_SECOND;

	private static final String PRAGMA_REGEX = "\\s*pragma.*";
	private static final String IMPORT_REGEX = "\\s*import[^\"]*\"[^\"]*\".*";

	private final EthereumRpc rpc;
	private final Address fromAccount;
	private final TransactionPoller transactionPoller;
	private final BlockPoller blockPoller;
	private final Timer transactionSubmitTimer = new Timer("TransactionSubmitTimer", true);
	private final BlockingQueue<TransactionAndReceipt> transactionQueue = new LinkedBlockingQueue<>();
	private final AtomicLong accountNonce;

	public Ethereum(final String accountPassword) {
		this(accountPassword, DEFAULT_SOCKET_ADDRESS);
	}

	public Ethereum(final String accountPassword, final InetSocketAddress socketAddress) {
		this(accountPassword, socketAddress, DEFAULT_BLOCK_CONFIRMATION_COUNT);
	}

	public Ethereum(final String accountPassword, final InetSocketAddress socketAddress,
			final int blockConfirmationCount) {
		this(accountPassword, socketAddress, blockConfirmationCount, DEFAULT_ACCOUNT);
	}

	public Ethereum(final String accountPassword, final InetSocketAddress socketAddress,
			final int blockConfirmationCount, final String account) {
		if (accountPassword == null || socketAddress == null) {
			throw new IllegalArgumentException();
		}

		try {
			LOGGER.info("Connecting to geth at " + socketAddress);

			rpc = waitFor(() -> (getRpcClient(socketAddress)), "Waiting for geth rpc " + socketAddress, LOGGER);

			if (account == DEFAULT_ACCOUNT) {
				fromAccount = getDefaultAccount(accountPassword);
			} else {
				fromAccount = new Address(account);
			}

			LOGGER.info("Using account " + fromAccount + ", unlocking ...");

			rpc.personal_unlockAccount(fromAccount, accountPassword, 31536000);
			LOGGER.info("Successfully unlocked");

			accountNonce = new AtomicLong(new Quantity(rpc.eth_getTransactionCount(fromAccount, "latest")).longValue());
			LOGGER.info("Set nonce to: " + accountNonce.longValue());

			final PendingEvents pendingEvents = new PendingEvents(blockConfirmationCount);

			this.transactionPoller = schedule(TransactionPoller.class.getSimpleName(), new TransactionPoller(rpc),
					POLL_FREQUENCY_MILLIS);
			this.blockPoller = schedule(BlockPoller.class.getSimpleName(), new BlockPoller(rpc, pendingEvents),
					POLL_FREQUENCY_MILLIS);
			this.transactionSubmitTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						final TransactionAndReceipt transactionAndReceipt = transactionQueue.take();

						try {
							final String transactionHash = rpc
									.eth_sendTransaction(transactionAndReceipt.getTransaction());

							transactionPoller.addPendingTransaction(transactionHash, transactionAndReceipt);
						} catch (final JsonRpcException e) {
							final boolean retry = transactionAndReceipt.failed(e);

							LOGGER.warn("Failed to submit transaction [e=" + e + ", retry=" + retry + "]");

							if (retry) {
								transactionQueue.add(transactionAndReceipt);
							}
						}
					} catch (final InterruptedException e) {
						// Exit - this is a daemon timer
					}
				}
			}, 0, TRANSACTION_DELAY);
		} catch (final RuntimeException e) {
			throw e;
		} catch (InterruptedException | TimeoutException e) {
			throw new IllegalStateException(e);
		}
	}

	private Address getDefaultAccount(String accountPassword) {
		final Address account;

		final List<String> accounts = rpc.eth_accounts();

		if (accounts.isEmpty()) {
			LOGGER.info("There are no accounts, creating one ...");

			account = rpc.personal_newAccount(accountPassword);
		} else {
			account = rpc.eth_coinbase();
		}

		return account;
	}

	private EthereumRpc getRpcClient(final InetSocketAddress socketAddress) {
		try {
			final JsonRpcTransport transport = new HttpJsonRpcTransport(socketAddress);

			final EthereumRpc rpcClient = new JsonRpcFactory(transport).newClient(EthereumRpc.class, TIMEOUT_SECONDS,
					TimeUnit.SECONDS);

			LOGGER.info("Connected " + rpcClient.web3_clientVersion());

			return rpcClient;
		} catch (final RuntimeException e) {
			LOGGER.warn("Failed to connect to [" + socketAddress + "] " + e.getMessage());

			return null;
		}
	}

	public CompilationResults compileContract(final String contract) {
		try {
			return rpc.eth_compileSolidity(
					readContractInto(new StringBuilder(), contract, true, new HashSet<>()).toString());
		} catch (final IOException e) {
			throw new IllegalStateException("Failed reading contracts", e);
		}
	}

	public CompletionStage<TransactionReceipt> newContract(final String code, final FunctionEncoding constructor,
			final Object... args) {
		final Transaction transaction = new Transaction();

		transaction.setFrom(fromAccount);
		transaction.setData(code + (constructor != null ? constructor.encode(args) : ""));
		transaction.setGas(NEW_CONTRACT_GAS);
		transaction.setNonce("0x" + Long.toHexString(accountNonce.getAndIncrement()));

		final TransactionAndReceipt transactionAndReceipt = new TransactionAndReceipt(transaction);

		final String transactionHash = rpc.eth_sendTransaction(transaction);

		transactionPoller.addPendingTransaction(transactionHash, transactionAndReceipt);

		return transactionAndReceipt.getFutureTransactionReceipt();
	}

	public CompletionStage<TransactionReceipt> sendTransaction(final Address contractAddress,
			final FunctionEncoding functionEncoding, final Object... args) {
		final Transaction transaction = new Transaction();

		transaction.setFrom(fromAccount);
		transaction.setTo(contractAddress);
		transaction.setValue(ZERO_VALUE);
		transaction.setGas(MAIN_GAS);
		transaction.setData(functionEncoding.encode(args));
		transaction.setNonce("0x" + Long.toHexString(accountNonce.getAndIncrement()));

		final TransactionAndReceipt transactionAndReceipt = new TransactionAndReceipt(transaction);

		transactionQueue.add(transactionAndReceipt);

		return transactionAndReceipt.getFutureTransactionReceipt();
	}

	public Object[] call(final Address contractAddress, final FunctionEncoding functionEncoding, final Object... args) {
		final Transaction transaction = new Transaction();

		transaction.setFrom(fromAccount);
		transaction.setTo(contractAddress);
		transaction.setValue(ZERO_VALUE);
		transaction.setData(functionEncoding.encode(args));

		transaction.setGas(MAIN_GAS);
		transaction.setGasPrice(ZERO_VALUE);

		return functionEncoding.decode(rpc.eth_call(transaction, "latest"));
	}

	public void addEventListener(final FunctionEncoding functionEncoding, final Consumer<Object[]> handler,
			final String... additionalTopics) {
		final List<String> topics = new ArrayList<>();

		topics.add(functionEncoding.getHash());

		topics.addAll(Arrays.asList(additionalTopics));

		blockPoller.addEventListener(topics, functionEncoding, handler);
	}

	public Address getFromAccount() {
		return fromAccount;
	}

	public String getEnode() {
		return rpc.admin_nodeInfo().getEnode();
	}

	public void waitForETH() throws InterruptedException, TimeoutException {
		waitForETH(DEFAULT_TIMEOUT);
	}

	public void waitForETH(final long timeoutMillis) throws InterruptedException, TimeoutException {
		waitFor(() -> (rpc.eth_getBalance(fromAccount, "latest").equals(ZERO_VALUE) ? null : Void.class),
				"Waiting for account to have some ETH", LOGGER);
	}

	public boolean hasContractAtAddress(final Address contractAddress) {
		return contractAddress != null && !rpc.eth_getCode(contractAddress, "latest").equals("0x");
	}

	public void waitForContractAtAddress(final Address contractAddress) {
		if (contractAddress.isZero()) {
			throw new IllegalArgumentException();
		}

		try {
			waitFor(() -> (hasContractAtAddress(contractAddress) ? Void.class : null),
					"Waiting for code to be installed at " + contractAddress, LOGGER);
		} catch (InterruptedException | TimeoutException e) {
			throw new IllegalStateException(e);
		}
	}

	public void addPeer(final String enode) {
		rpc.admin_addPeer(enode);
	}

	public void startMining() {
		startMining(DEFAULT_THREADS);
	}

	public void startMining(final int threads) {
		if (threads <= 0) {
			throw new IllegalArgumentException();
		}

		if (!rpc.eth_mining()) {
			LOGGER.info("Starting mining ...");

			rpc.miner_start(threads);

			LOGGER.info("Started mining");
		}
	}

	public void stopMining() {
		if (rpc.eth_mining()) {
			LOGGER.info("Stopping mining ...");

			rpc.miner_stop();

			LOGGER.info("Stopped mining");
		}
	}

	@Override
	public void close() throws IOException {
		transactionPoller.cancel();
		blockPoller.cancel();
	}

	private StringBuilder readContractInto(final StringBuilder stringBuilder, final String contractResource,
			final boolean root, final Set<String> resources) throws IOException {
		if (!resources.contains(contractResource)) {
			resources.add(contractResource);

			try (final BufferedReader reader = new BufferedReader(
					new InputStreamReader(Ethereum.class.getResourceAsStream(contractResource)))) {

				String line = reader.readLine();

				while (line != null) {

					if (line.matches(PRAGMA_REGEX)) {
						if (root) {
							stringBuilder.append(line);
						}
					} else if (line.matches(IMPORT_REGEX)) {
						final String importContract = line.substring(line.indexOf('"') + 1, line.lastIndexOf('"'));

						final String newContractResource = contractResource.indexOf('/') != -1
								? contractResource.substring(0, contractResource.lastIndexOf('/') + 1) + importContract
								: importContract;

						readContractInto(stringBuilder, newContractResource, false, resources);
					} else {
						stringBuilder.append(line);
					}

					line = reader.readLine();
				}
			} catch (final NullPointerException e) {
				throw new FileNotFoundException(contractResource + " (via getResourceAsStream())");
			}
		}

		return stringBuilder;
	}

	private <T extends TimerTask> T schedule(final String name, final T task, final long frequencyMillis) {
		final Timer timer = new Timer(name, true);

		timer.scheduleAtFixedRate(task, 0, frequencyMillis);

		return task;
	}
}