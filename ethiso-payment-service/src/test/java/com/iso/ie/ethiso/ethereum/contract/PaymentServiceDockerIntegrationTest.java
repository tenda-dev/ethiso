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
package com.iso.ie.ethiso.ethereum.contract;

import static com.iso.ie.ethiso.DockerUtils.getDockerHostName;
import static com.iso.ie.ethiso.domain.Bic.bic;
import static com.iso.ie.ethiso.domain.Currency.GBP;
import static com.iso.ie.ethiso.domain.Iban.iban;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rabbitmq.client.ConnectionFactory;
import com.iso.ie.ethiso.PaymentListener;
import com.iso.ie.ethiso.PaymentService;
import com.iso.ie.ethiso.domain.Bic;
import com.iso.ie.ethiso.domain.Iban;
import com.iso.ie.ethiso.domain.Party;
import com.iso.ie.jsonrpc.JsonRpcFactory;
import com.iso.ie.jsonrpc.rabbitmq.RabbitMQJsonRpcTransport;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerException;


public class PaymentServiceDockerIntegrationTest {

	private static final int ONE_SECOND_IN_MILLIS = 1000;
	private static final long TIMEOUT = 60;

	private static final Iban BANK_1_IBAN = iban("13GB234");
	private static final Bic BANK_1_BIC = bic("123BIC");

	private static final Iban BANK_2_IBAN = iban("56NL789");
	private static final Bic BANK_2_BIC = bic("567BIC");

	private static final Bic UNREGISTERED_BIC = bic("unregistered");

	private static String ipAddress;
	
	private static PaymentService paymentServiceBank1;
	private static PaymentService paymentServiceBank2;

	private static final Party bank1Coinbase = Party.party("0x0463613a85c45e8fc55c14622aa85da8109b0e56");
	private static final Party bank2Coinbase = Party.party("0x12eded27258da8202bad33ad307664a6f596a361");
	
	private static final Properties properties = new Properties();
	private static int port;

	static {
		((Jdk14Logger) LogFactory.getLog("org.apache.http")).getLogger().setLevel(Level.WARNING);
	}

	@BeforeClass
	public static void setUp() throws IOException, InterruptedException, DockerCertificateException, DockerException {
				
		ipAddress = getDockerHostName();
		
		logOutput("Docker Host name:" + ipAddress);
		
		final ConnectionFactory connectionFactory = waitForRabbitConnection();

		logOutput("waiting for rabbit connection");
		
		final JsonRpcFactory bank1JsonRpcFactory = new JsonRpcFactory(
				new RabbitMQJsonRpcTransport(connectionFactory, "payment-service-a"));
		paymentServiceBank1 = bank1JsonRpcFactory.newClient(PaymentService.class, TIMEOUT, SECONDS);
		
		logOutput("got connection to payment-service-a");

		final JsonRpcFactory bank2JsonRpcFactory = new JsonRpcFactory(
				new RabbitMQJsonRpcTransport(connectionFactory, "payment-service-b"));
		
		bank2JsonRpcFactory.registerCallbackType(PaymentListener.class);
		paymentServiceBank2 = bank2JsonRpcFactory.newClient(PaymentService.class, TIMEOUT, SECONDS);

		logOutput("got connection to payment-service-b");
		
		paymentServiceBank1.registerBic(BANK_1_BIC);
		
		logOutput("registered payment-service-a BIC :" + BANK_1_BIC);
		
		paymentServiceBank2.registerBic(BANK_2_BIC);
		
		logOutput("registered payment-service-b" + BANK_2_BIC);
		
		paymentServiceBank2.setLimit(bank1Coinbase, GBP, new BigInteger("10"));
		
		logOutput("Set limit for payment-service-b");
		
		paymentServiceBank1.setAllow(bank2Coinbase, GBP, new BigInteger("10"));
		
		logOutput("Set allow for payment-service-a");
	}

	@Test
	public void shouldBeAbleToMakePaymentsAndReceiveEvent()
			throws InterruptedException, TimeoutException, ExecutionException {

		CompletableFuture<Boolean> messageFuture1 = new CompletableFuture<>();

		paymentServiceBank2.registerPaymentListener((fromBic, toBic, from, to, currency, paymentAmount, additionalProperties) -> {
			assertThat(fromBic, is(BANK_1_BIC));
			assertThat(toBic, is(BANK_2_BIC));
			assertThat(from, is(BANK_1_IBAN));
			assertThat(to, is(BANK_2_IBAN));
			assertThat(additionalProperties, is(Matchers.notNullValue()));
			assertThat(additionalProperties.size(), is(5));
			assertThat(additionalProperties.get("a"), is("property"));
			messageFuture1.complete(true);
		});

		paymentServiceBank1.sendPaymentAsync(BANK_1_BIC, BANK_2_BIC, BANK_1_IBAN, BANK_2_IBAN, GBP, new BigInteger("5"),
				Collections.singletonMap("a", "property"));

		messageFuture1.get(TIMEOUT, SECONDS);

		assertThat(paymentServiceBank1.getBalance(bank2Coinbase, GBP), is(new BigInteger("5")));

		CompletableFuture<Boolean> messageFuture2 = new CompletableFuture<>();

		paymentServiceBank2.registerPaymentListener((fromBic, toBic, fromIban, toIban, currency, paymentAmount, additionalProperties) -> {
			assertThat(fromBic, is(BANK_1_BIC));
			assertThat(toBic, is(BANK_2_BIC));
			assertThat(fromIban, is(BANK_1_IBAN));
			assertThat(toIban, is(BANK_2_IBAN));
			assertThat(additionalProperties, is(Matchers.notNullValue()));
			assertThat(additionalProperties.size(), is(5));
			assertThat(additionalProperties.get("a"), is("property"));
			messageFuture2.complete(true);
		});

		paymentServiceBank1.sendPayment(BANK_1_BIC, BANK_2_BIC, BANK_1_IBAN, BANK_2_IBAN, GBP, new BigInteger("5"),
				Collections.singletonMap("a", "property"));

		messageFuture2.get(TIMEOUT, SECONDS);

		assertThat(paymentServiceBank1.getBalance(bank2Coinbase, GBP), is(new BigInteger("10")));
	}

	@Test
	public void testAllTheSendPaymentErrorCases() {
		//  1 - need to pay a valid address
		//  2 - cannot pay ourselves
		//  4 - payment would exceed limits
		//  5 - cannot pay from address different to sender
		expectSendPaymentToThrow("need to pay a valid address", BANK_1_BIC, UNREGISTERED_BIC, "20");
		expectSendPaymentToThrow("cannot pay ourselves", BANK_1_BIC, BANK_1_BIC, "20");
		expectSendPaymentToThrow("payment would exceed limits", BANK_1_BIC, BANK_2_BIC, "20");
	}

	private void expectSendPaymentToThrow(String message, Bic from, Bic to, String amount) {
		try {
			paymentServiceBank1.sendPayment(from, to, BANK_1_IBAN, BANK_2_IBAN, GBP, new BigInteger(amount), null);
			fail(message);
		} catch (IllegalStateException e) {
			assertThat(e.getMessage(), is(message));
		}
	}

	private static ConnectionFactory waitForRabbitConnection() throws InterruptedException, IOException {
		final ConnectionFactory connectionFactory = new ConnectionFactory();
		
		properties.load(PaymentServiceDockerIntegrationTest.class.getResourceAsStream("/port.properties"));

		port = Integer.valueOf(properties.getProperty("rabbit.mq.port"));
		
		connectionFactory.setHost(ipAddress);
		connectionFactory.setPort(port);
		
		logOutput("rabbit IP address is: " + ipAddress);

		boolean ready = false;
		int count = 1;

		while (!ready) {
			try {
				connectionFactory.newConnection();
				ready = true;
			} catch (IOException | TimeoutException e) {
				if (count > TIMEOUT) {
					throw new IllegalStateException("Gave up waiting after " + TIMEOUT + " seconds");
				}
				logOutput("Failed to connect to Rabbit because: "+ e.toString());
				
				logOutput("Waiting another second for rabbit (total " + count++ + " seconds)");
				Thread.sleep(ONE_SECOND_IN_MILLIS);
			}
		}

		return connectionFactory;
	}
	
	private static void logOutput(String message){
		String fullOutput = "\nTestHost> " + message + "\n";
		
		System.out.println(fullOutput);
	}
}