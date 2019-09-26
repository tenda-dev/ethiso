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
package com.iso.ie.ethiso.echobot;

import com.rabbitmq.client.*;
import com.iso.ie.ethiso.PaymentListener;
import com.iso.ie.ethiso.PaymentService;
import com.iso.ie.ethiso.TrustlineListener;
import com.iso.ie.ethiso.domain.Bic;
import com.iso.ie.ethiso.domain.Currency;
import com.iso.ie.ethiso.domain.Iban;
import com.iso.ie.ethiso.domain.Party;
import com.iso.ie.jsonrpc.JsonRpcFactory;
import com.iso.ie.jsonrpc.JsonRpcTransport;
import com.iso.ie.jsonrpc.rabbitmq.RabbitMQJsonRpcTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.iso.ie.ethiso.domain.Bic.bic;

public class EchoBot {

	private static final Logger LOGGER = LoggerFactory.getLogger(EchoBot.class);

	private static Channel channel;

	public static void main(final String[] args) throws IOException, TimeoutException, InterruptedException {
		if (args.length == 3) {
			runBroker(args[0], args[1], args[2]);
		} else {
			System.err.println("Usage: " + EchoBot.class.getSimpleName()
					+ " <rabbit_host> <rpc_routing_key>");
		}
	}

	private static void runBroker(final String rabbitMqHost, final String jsonRpcRoutingKey, final String defaultBic)
			throws IOException, TimeoutException, InterruptedException {
		ConnectionFactory connectionFactory = new ConnectionFactory();

		LOGGER.info("Echobot listening to the queue routing key:"  + jsonRpcRoutingKey);

		connectionFactory.setHost(rabbitMqHost);

		final Connection connection = getConnection(connectionFactory);

		channel = connection.createChannel();

		addShutdownHook(connection, channel);

		final JsonRpcTransport transport = new RabbitMQJsonRpcTransport(connectionFactory, jsonRpcRoutingKey);

		final JsonRpcFactory jsonRpcFactory = new JsonRpcFactory(transport);

		final PaymentService paymentService = jsonRpcFactory.newClient(PaymentService.class, 10, TimeUnit.HOURS);
		

		jsonRpcFactory.registerCallbackType(PaymentListener.class);
		jsonRpcFactory.registerCallbackType(TrustlineListener.class);

		registerTrustlineListener(paymentService);
		registerPaymentListener(paymentService);
		
		setDefaultBic(paymentService, bic(defaultBic));

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

	private static void registerTrustlineListener(final PaymentService paymentService) {
		try {
			LOGGER.info("registering trustline listener and waiting for a while...");
			paymentService.registerTrustlineListener(new TrustlineListener() {

				@Override
				public void trustlineModified(Party them, Currency currency, BigInteger forwardLimit, BigInteger reverseLimit, BigInteger forwardAllow, BigInteger reverseAllow) {
					LOGGER.info("tl,them={},ccy={},fl={},rl={},fa={},ra={}", them, currency, forwardLimit, reverseLimit, forwardAllow, reverseAllow);
					
					if (!forwardAllow.equals(reverseLimit)) {
						
						LOGGER.info( "forwardAllow {} reverseLimit {}", forwardAllow, reverseLimit);
						LOGGER.info("updating {} limit for {} to reflect allow of {}", currency, them, forwardAllow);
						paymentService.setLimit(them, currency, forwardAllow); 
					}
					
					if (!forwardLimit.equals(reverseAllow)) {
						LOGGER.info("forwardLimit {} reverseAllow {}", forwardLimit, reverseAllow);
						LOGGER.info("updating {} allow for {} to reflect their limit of {}", currency, them, forwardLimit);
						paymentService.setAllow(them, currency, forwardLimit);
					}
				}

			});
		} catch (final IllegalStateException e) {
			LOGGER.error("Failed to register trustline listener");
		}
	}

	private static void registerPaymentListener(final PaymentService paymentService) {
		try {
			LOGGER.info("registering payment listener and waiting for a while...");
			paymentService.registerPaymentListener(new PaymentListener() {

				@Override
				public void paymentReceived(Bic fromBic, Bic toBic, Iban fromIban, Iban toIban, Currency currency, BigInteger paymentAmount, Map<String, Object> additionalProperties) {
					LOGGER.info("tk=end,fromBic={},toBic={},fromIban={},toIban={},amt={},ccy={},ref={}", fromBic, toBic, fromIban, toIban, paymentAmount.intValue(), currency, additionalProperties.get("RmtInfUstrd"));
					paymentService.sendPayment(toBic, fromBic, toIban, fromIban, currency, paymentAmount, additionalProperties);
				}

			});
		} catch (final IllegalStateException e) {
			LOGGER.error("Failed to register payment listener");
		}
	}

	private static Connection getConnection(ConnectionFactory connectionFactory)
			throws IOException, TimeoutException, InterruptedException {
		Connection connection = null;

		while (connection == null) {
			try {
				connection = connectionFactory.newConnection();
			} catch (final ConnectException e) {
				LOGGER.warn("RabbitMQ not yet available will try again shortly");

				Thread.sleep(2000);
			}
		}

		return connection;
	}
	
	private static void setDefaultBic(final PaymentService paymentService, Bic bic){
		
		LOGGER.info("Echobot setting default Iban to: {} ", bic.toString());
		
		if(paymentService.registerBic(bic)){
			LOGGER.info("Echobot succesfully set default bic to: {} ", bic.toString());
		} else {
			LOGGER.error("Echobot failed to set default bic to: {} ",bic.toString());
		}		
	}
}
