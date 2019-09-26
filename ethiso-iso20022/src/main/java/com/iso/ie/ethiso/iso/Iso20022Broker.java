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
package com.iso.ie.ethiso.iso;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.iso.ie.ethiso.PaymentListener;
import com.iso.ie.ethiso.PaymentService;
import com.iso.ie.jsonrpc.JsonRpcFactory;
import com.iso.ie.jsonrpc.JsonRpcTransport;
import com.iso.ie.jsonrpc.rabbitmq.RabbitMQJsonRpcTransport;

public class Iso20022Broker {

	private static final Logger LOGGER = LoggerFactory.getLogger(Iso20022Broker.class);

	static final int RABBIT_PORT = 5672;

	static final String SCF = "/scf";
	static final String ICF = "/icf";
	static final String CVF = "/cvf";
	static final String EXCHANGE = "";

	private Channel channel;
	private String isoRoutingKey;

	public Iso20022Broker(String isoRoutingKey) {
		this.isoRoutingKey = isoRoutingKey;
	}

	public static void main(final String[] args) throws IOException, TimeoutException, InterruptedException {
		if (args.length == 3 || args.length == 4) {

			new Iso20022Broker(args[1]).runBroker(args[0], args[2],
					args.length == 4 ? Integer.valueOf(args[3]) : RABBIT_PORT);
		} else {
			System.err.println("Usage: " + Iso20022Broker.class.getSimpleName()
					+ " <rabbit_host> <iso_routing_key> <rpc_routing_key> [port]");
		}
	}

	private void runBroker(final String rabbitMqHost, final String jsonRpcRoutingKey, final int port)
			throws IOException, TimeoutException, InterruptedException {
		ConnectionFactory connectionFactory = new ConnectionFactory();

		connectionFactory.setHost(rabbitMqHost);
		connectionFactory.setPort(port);
		
		final Connection connection = getConnection(connectionFactory);

		channel = connection.createChannel();

		addShutdownHook(connection, channel);

		channel.queueDeclare(isoRoutingKey + ICF, false, false, false, null);
		channel.queueDeclare(isoRoutingKey + CVF, false, false, false, null);
		channel.queueDeclare(isoRoutingKey + SCF, false, false, false, null);

		final JsonRpcTransport transport = new RabbitMQJsonRpcTransport(connectionFactory, jsonRpcRoutingKey);

		final JsonRpcFactory jsonRpcFactory = new JsonRpcFactory(transport);

		final PaymentService paymentService = jsonRpcFactory.newClient(PaymentService.class, 10, TimeUnit.HOURS);

		jsonRpcFactory.registerCallbackType(PaymentListener.class);

		registerListener(paymentService);

		registerIso20022Consumer(paymentService);
	}

	private static void addShutdownHook(final Connection connection, final Channel channel) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("Shutting down ...");

				try {
					if (channel != null) {
						channel.close();
					}
				} catch (IOException | TimeoutException e) {
					LOGGER.warn("Failed to close channel");
				}

				try {
					if (connection != null) {
						connection.close();
					}
				} catch (final IOException e) {
					LOGGER.warn("Failed to close connection");
				}
			}
		}, "ShutdownHook-RabbitMQ"));
	}

	private void registerIso20022Consumer(final PaymentService paymentService) throws IOException {
		channel.basicConsume(isoRoutingKey + ICF, true,
				new CreditTransferConsumer(paymentService, channel, isoRoutingKey + CVF));
	}

	private void registerListener(final PaymentService paymentService) {
		try {
			LOGGER.info("registering payment listener and waiting for a while...");
			paymentService.registerPaymentListener(new IsoPaymentListener(channel, isoRoutingKey + SCF));
		} catch (final IllegalStateException e) {
			LOGGER.error("Failed to register payment listener [" + e + "]");
		}
	}

	private Connection getConnection(ConnectionFactory connectionFactory) throws InterruptedException {
		Connection connection = null;

		while (connection == null) {
			try {
				connection = connectionFactory.newConnection();
			} catch (final IOException | TimeoutException e) {
				LOGGER.warn("RabbitMQ not yet available will try again shortly");

				Thread.sleep(1000);
			}
		}

		return connection;
	}
}