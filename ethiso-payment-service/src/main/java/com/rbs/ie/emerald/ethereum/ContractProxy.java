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
package com.rbs.ie.ethiso.ethereum;

import static com.rbs.ie.ethiso.ethereum.FunctionEncoding.getFunctionEncodings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbs.ie.ethiso.ethereum.rpc.Address;
import com.rbs.ie.ethiso.ethereum.rpc.Function;
import com.rbs.ie.ethiso.ethereum.rpc.TransactionReceipt;

public abstract class ContractProxy {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractProxy.class);

	private final String contractName;
	private final Ethereum ethereum;
	private final Map<String, FunctionEncoding> functionEncodings;
	private Address contractAddress;
	protected boolean hasFallback = false;

	protected ContractProxy(final Ethereum ethereum, final Mode mode, final Address contractAddress,
			final String contract, final Object... constructorArgs) {
		if (ethereum == null || contract == null || contract.isEmpty()) {
			throw new IllegalArgumentException();
		}

		this.ethereum = ethereum;
		this.contractName = contract;
		this.contractAddress = contractAddress;
		this.functionEncodings = getFunctionEncodings(getABI(contract));

		switch (mode) {
		case WAIT:
			ethereum.waitForContractAtAddress(contractAddress);
			break;

		case INSTALL:
			ensureContractInstalled(getCode(contract), constructorArgs);
			break;

		default:
			throw new UnsupportedOperationException("Unknown mode [" + mode + "]");
		}

		LOGGER.info("Using [" + contractName + "] at contract address [" + this.contractAddress + "]");
	}

	public Address getContractAddress() {
		return contractAddress;
	}

	protected Object[] call(final String name, final Object... args) {
		final FunctionEncoding functionEncoding = functionEncodings.get(name);

		if (functionEncoding == null) {
			throw new IllegalArgumentException("No such method [" + name + "]");
		}

		return ethereum.call(contractAddress, functionEncoding, args);
	}

	protected void addEventListener(final String name, final Consumer<Object[]> handler,
			final String... additionalTopics) {
		final FunctionEncoding functionEncoding = functionEncodings.get(name);

		if (functionEncoding == null) {
			throw new IllegalArgumentException("No such event [" + name + "]");
		}

		ethereum.addEventListener(functionEncoding, handler, additionalTopics);
	}

	protected CompletionStage<TransactionReceipt> sendTransaction(final String name, final Object... args) {
		final FunctionEncoding functionEncoding = functionEncodings.get(name);

		if (functionEncoding == null) {
			throw new IllegalArgumentException("No such method [" + name + "]");
		}

		return ethereum.sendTransaction(contractAddress, functionEncoding, args);
	}

	private void ensureContractInstalled(final String code, final Object... args) {
		if (contractAddress != null && !ethereum.hasContractAtAddress(contractAddress)) {
			LOGGER.warn("[" + contractName + "] not found at address [" + contractAddress + "]");

			contractAddress = null;
		}

		if (contractAddress == null) {
			try {
				final TransactionReceipt transactionReceipt = ethereum
						.newContract(code, functionEncodings.get(null), args).toCompletableFuture().get();

				if (!ethereum.hasContractAtAddress(transactionReceipt.getContractAddress())) {
					throw new IllegalStateException(
							"Failed to install [" + contractName + "] contract, not enough gas?");
				}

				contractAddress = transactionReceipt.getContractAddress();
			} catch (InterruptedException | ExecutionException e) {
				throw new IllegalStateException("Failed to install [" + contractName + "] contract", e);
			}
		}
	}

	public enum Mode {
		/**
		 * We should wait until the code is available at the given contract
		 * address
		 */
		WAIT,

		/**
		 * We should install the code if it is not available at the given
		 * contract address
		 */
		INSTALL
	}

	private List<Function> getABI(final String contract) {
		final String abiResource = "/contracts/" + contract + ".abi";

		try (final InputStream inputStream = ContractProxy.class.getResourceAsStream(abiResource)) {
			return new ObjectMapper().readValue(inputStream, new TypeReference<List<Function>>() {
			});
		} catch (final IOException e) {
			throw new IllegalArgumentException("Error reading ABI " + abiResource, e);
		}
	}

	private String getCode(final String contract) {
		final String binaryResource = "/contracts/" + contract + ".bin";

		try (final BufferedReader reader = new BufferedReader(
				new InputStreamReader(ContractProxy.class.getResourceAsStream(binaryResource)))) {
			final StringBuilder stringBuilder = new StringBuilder();

			String line = reader.readLine();

			while (line != null) {
				stringBuilder.append(line);

				line = reader.readLine();
			}

			return stringBuilder.toString();
		} catch (final IOException e) {
			throw new IllegalArgumentException("Error reading code " + binaryResource, e);
		}
	}
}