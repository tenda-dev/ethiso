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

import static com.rbs.ie.ethiso.ethereum.ContractProxy.Mode.INSTALL;
import static com.rbs.ie.ethiso.ethereum.ContractProxy.Mode.WAIT;
import static com.rbs.ie.ethiso.ethereum.Utils.waitFor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.ConnectionFactory;
import com.rbs.ie.ethiso.PaymentListener;
import com.rbs.ie.ethiso.PaymentService;
import com.rbs.ie.ethiso.TrustlineListener;
import com.rbs.ie.ethiso.api.Configuration;
import com.rbs.ie.ethiso.api.ConfigurationService;
import com.rbs.ie.ethiso.ethereum.ContractProxy.Mode;
import com.rbs.ie.ethiso.ethereum.contract.GlobalRegistrar;
import com.rbs.ie.ethiso.ethereum.contract.NameToAddressRegistrar;
import com.rbs.ie.ethiso.ethereum.contract.PaymentServiceImpl;
import com.rbs.ie.ethiso.ethereum.rest.RestPaymentService;
import com.rbs.ie.ethiso.ethereum.rpc.Address;
import com.rbs.ie.jsonrpc.JsonRpcFactory;
import com.rbs.ie.jsonrpc.http.HttpJsonRpcTransport;
import com.rbs.ie.jsonrpc.rabbitmq.RabbitMQJsonRpcTransport;

public class PaymentServiceMain {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceMain.class);

	private static final String BLOCK_CONFIRMATION_COUNT_PROPERTY = "BLOCK_CONFIRMATION_COUNT";

	private static final int DEFAULT_BLOCK_CONFIRMATION_COUNT = 15;

	private static final String FILE_PROTOCOL = "file";

	public static final String CONFIG_RPC_PATH = "/config";

	private static final String PAYMENT_SERVICE_KEY = "payment-service";
	private static final String BIC_REGISTRAR_KEY = "bic-registrar";

	public PaymentServiceMain(final String rabbitHost, final String routingKey, final InetSocketAddress gethRpc,
			final String accountPassword, final int restPort, final int configPort, final URL configServiceURL)
			throws InterruptedException, TimeoutException {
		final String bccProperty = System.getenv(BLOCK_CONFIRMATION_COUNT_PROPERTY);
		
		
		final int blockConfirmationCount = bccProperty != null? Integer.parseInt(bccProperty) : DEFAULT_BLOCK_CONFIRMATION_COUNT;

		LOGGER.info("Block confirmation count set to [" + blockConfirmationCount + "]");
		
		final Ethereum ethereum = new Ethereum(accountPassword, gethRpc, blockConfirmationCount);

		String registrarAddress = null;

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				ethereum.stopMining();

				try {
					ethereum.close();
				} catch (IOException e) {
					LOGGER.warn("Failed to close ethereum", e);
				}
			}
		});

		Mode contractInstallMode = WAIT;

		if (configServiceURL != null) {
			LOGGER.info("We are a secondary, connecting to config service at [" + configServiceURL + "]");

			final Configuration configuration = getConfiguration(configServiceURL);

			ethereum.addPeer(configuration.getEnode());

			registrarAddress = configuration.getContractRegistrar();
		} else {
			LOGGER.info("We are the primary");

			contractInstallMode = INSTALL;
		}

		ethereum.startMining();

		ethereum.waitForETH();
		
	  	final Address paymentServiceEthereumAddress = ethereum.getFromAccount();
	  	
	  	LOGGER.info("Payment Service Address: " + paymentServiceEthereumAddress.toString() );
	  	
		final GlobalRegistrar globalRegistrar = new GlobalRegistrar(ethereum, contractInstallMode,
				new Address(registrarAddress));

		final NameToAddressRegistrar bicRegistrar = getBicRegistrar(ethereum, contractInstallMode, globalRegistrar,
				getAddress(contractInstallMode, globalRegistrar, BIC_REGISTRAR_KEY));

		final PaymentService paymentService = getPaymentService(ethereum, contractInstallMode, globalRegistrar,
				getAddress(contractInstallMode, globalRegistrar, PAYMENT_SERVICE_KEY), bicRegistrar);

		final ConfigurationService configurationService = new ConfigurationServiceImpl(ethereum,
				globalRegistrar.getContractAddress().toString());

		registerConfigurationService(configPort, configurationService);

		registerRestEndpoint(restPort, paymentService, bicRegistrar);

		paymentService.registerPaymentListener((fb, tb, fi, ti, c, a, p) -> {
			LOGGER.info("Received payment message [fb=" + fb + ", tb=" + tb + ", fi=" + fi + ", ti=" + ti + ", c=" + c
					+ ", a=" + a + ", p=" + p + "]");
		});

		registerRabbitEndpoint(paymentService, rabbitHost, routingKey);
	}

	static Configuration getConfiguration(final URL configServiceURL) throws InterruptedException {
		Configuration configuration;

		if (FILE_PROTOCOL.equals(configServiceURL.getProtocol())) {
			configuration = new StaticConfigurationService().getConfiguration(configServiceURL.toExternalForm());
		} else {
			final InetSocketAddress socketAddress = InetSocketAddress.createUnresolved(configServiceURL.getHost(),
					configServiceURL.getPort());

			final ConfigurationService configurationService = new JsonRpcFactory(
					new HttpJsonRpcTransport(socketAddress, configServiceURL.getPath()))
							.newClient(ConfigurationService.class, 30, TimeUnit.SECONDS);

			try {
				configuration = waitFor(() -> {
					try {
						return configurationService.getConfiguration(configServiceURL.toString());
					} catch (final RuntimeException e) {
						return null;
					}
				}, "Waiting for configuration service at [" + configServiceURL + "]", LOGGER);
			} catch (final TimeoutException e) {
				throw new IllegalStateException(e);
			}
		}

		return configuration;
	}

	private void registerConfigurationService(final int configPort, final ConfigurationService configurationService) {
		final JsonRpcFactory jsonRpcFactory = new JsonRpcFactory(
				new HttpJsonRpcTransport(InetSocketAddress.createUnresolved("localhost", configPort), CONFIG_RPC_PATH));

		jsonRpcFactory.registerService(ConfigurationService.class, configurationService);
	}

	private void registerRestEndpoint(final int restPort, final PaymentService paymentService,
			NameToAddressRegistrar bicRegistrar) {
		new RestPaymentService().registerRestEndpoints(restPort, paymentService, bicRegistrar);
	}

	private void registerRabbitEndpoint(final PaymentService paymentService, final String rabbitHost,
			final String routingKey) {
		try {
			final ConnectionFactory connectionFactory = new ConnectionFactory();

			connectionFactory.setHost(rabbitHost);

			final JsonRpcFactory rabbit = new JsonRpcFactory(
					new RabbitMQJsonRpcTransport(connectionFactory, routingKey));

			rabbit.registerCallbackType(PaymentListener.class);
			rabbit.registerCallbackType(TrustlineListener.class);

			rabbit.registerService(PaymentService.class, paymentService);
		} catch (final RuntimeException e) {
			LOGGER.warn("Failed to register rabbit endpoint " + e);
		}
	}

	private PaymentService getPaymentService(final Ethereum ethereum, final Mode mode,
			final GlobalRegistrar globalRegistrar, final Address paymentServiceAddress,
			NameToAddressRegistrar bicRegistrar) {
		final PaymentServiceImpl paymentService = new PaymentServiceImpl(ethereum, mode, paymentServiceAddress,
				bicRegistrar);

		if (!paymentServiceAddress.equals(paymentService.getContractAddress())) {
			globalRegistrar.setAddress(PAYMENT_SERVICE_KEY, paymentService.getContractAddress());
		}

		return paymentService;
	}

	private NameToAddressRegistrar getBicRegistrar(final Ethereum ethereum, final Mode mode,
			final GlobalRegistrar globalRegistrar, Address bicRegistrarAddress) {
		final NameToAddressRegistrar bicRegistrar = new NameToAddressRegistrar(ethereum, mode, bicRegistrarAddress);

		if (!bicRegistrarAddress.equals(bicRegistrar.getContractAddress())) {
			globalRegistrar.setAddress(BIC_REGISTRAR_KEY, bicRegistrar.getContractAddress());
		}

		return bicRegistrar;
	}

	/**
	 * Get the given contract address from the {@link GlobalRegistrar}. If the
	 * key returned is the zero address then wait (this implies that our geth
	 * node is still syncing).
	 */
	private static Address getAddress(final Mode contractInstallMode, final GlobalRegistrar globalRegistrar,
			final String key) throws InterruptedException, TimeoutException {
		Address address;

		switch (contractInstallMode) {
		case INSTALL:
			address = globalRegistrar.getAddress(key);
			break;

		case WAIT:
			address = waitFor(() -> {
				final Address candidateAddress = globalRegistrar.getAddress(key);

				return candidateAddress.isZero() ? null : candidateAddress;
			}, "Waiting for geth to sync GlobalRegistrar entry for [" + key + "]", LOGGER);
			break;
		default:
			throw new IllegalArgumentException("Unsupported contract install mode [" + contractInstallMode + "]");
		}

		return address;
	}

	public static void main(final String args[]) throws Exception {

		if (args.length == 6 || args.length == 7) {
			final String rabbitHost = args[0];
			final String routingKey = args[1];
			final InetSocketAddress gethRpc = asSocketAddress(args[2]);
			final String accountPassword = args[3];
			final int restPort = Integer.valueOf(args[4]);
			final int configPort = Integer.valueOf(args[5]);

			URL configServiceUrl = null;

			if (args.length > 6) {
				configServiceUrl = new URL(args[6]);
			}

			new PaymentServiceMain(rabbitHost, routingKey, gethRpc, accountPassword, restPort, configPort,
					configServiceUrl);
		} else {
			printUsage();
		}
	}

	private static void printUsage() {
		try (final BufferedReader reader = new BufferedReader(
				new InputStreamReader(PaymentServiceMain.class.getResourceAsStream("/usage.txt")))) {

			String line = reader.readLine();

			while (line != null) {
				System.err.println(line);

				line = reader.readLine();
			}
		} catch (final IOException e) {
			throw new IllegalStateException("Failed to print usage", e);
		}
	}

	private static InetSocketAddress asSocketAddress(final String hostAndPort) {
		final String[] hostSplit = hostAndPort.split(":");

		if (hostSplit.length != 2) {
			throw new IllegalArgumentException("Invalid host and port [" + hostAndPort + "]");
		}

		return InetSocketAddress.createUnresolved(hostSplit[0], Integer.valueOf(hostSplit[1]));
	}
}