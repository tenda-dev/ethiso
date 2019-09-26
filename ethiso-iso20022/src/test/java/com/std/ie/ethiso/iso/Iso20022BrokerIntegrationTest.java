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
package com.std.ie.ethiso.iso;

import static com.std.ie.ethiso.DockerUtils.getDockerHostName;
import static com.std.ie.ethiso.domain.Bic.bic;
import static com.std.ie.ethiso.domain.Iban.iban;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.std.ie.ethiso.PaymentListener;
import com.std.ie.ethiso.PaymentService;
import com.std.ie.ethiso.domain.Currency;
import com.std.ie.ethiso.iso.domain.CreditTransfer;
import com.std.ie.jsonrpc.JsonRpcFactory;
import com.std.ie.jsonrpc.rabbitmq.RabbitMQJsonRpcTransport;

public class Iso20022BrokerIntegrationTest {

	private static final PaymentService paymentServiceMock = mock(PaymentService.class);

	private static final String FROM_BIC = "fromBic";
	private static final String FROM_IBAN = "fromIban";
	private static final String TO_BIC = "toBic";
	private static final String TO_IBAN = "toIban";
	private static final BigDecimal AMOUNT = new BigDecimal("100.00");
	private static final String CURRENCY = "GBP";
	
	private static final Properties properties = new Properties();

	private static Channel channel;
	private static int port;
	
	@BeforeClass
	public static void setUp() throws IOException, TimeoutException, InterruptedException {
		final ConnectionFactory connectionFactory = new ConnectionFactory();

		properties.load(Iso20022BrokerIntegrationTest.class.getResourceAsStream("/ports.properties"));

		port = Integer.valueOf(properties.getProperty("rabbit.mq.port"));
		
		connectionFactory.setHost(getDockerHostName());
		connectionFactory.setPort(port);

		channel = connectionFactory.newConnection().createChannel();

		final RabbitMQJsonRpcTransport transport = new RabbitMQJsonRpcTransport(connectionFactory, "json-rpc");

		final JsonRpcFactory factory = new JsonRpcFactory(transport);

		factory.registerCallbackType(PaymentListener.class);

		factory.registerService(PaymentService.class, paymentServiceMock);
	}

	@Test
	public void runIsoBroker() throws IOException, TimeoutException, InterruptedException, ExecutionException {
		final CompletableFuture<PaymentListener> paymentListenerFuture = new CompletableFuture<>();

		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(final InvocationOnMock invocation) throws Throwable {
				paymentListenerFuture.complete((PaymentListener) invocation.getArguments()[0]);
				return null;
			}

		}).when(paymentServiceMock).registerPaymentListener(any());


		Iso20022Broker.main(new String[] { getDockerHostName(), "iso-broker", "json-rpc", properties.getProperty("rabbit.mq.port", String.valueOf(Iso20022Broker.RABBIT_PORT)) });

		verify(paymentServiceMock, atLeastOnce()).registerPaymentListener(any());

		final PaymentListener paymentListener = paymentListenerFuture.get();

		assertNotNull(paymentListener);

		final CompletableFuture<Object[]> sendPaymentResult = new CompletableFuture<>();

		final CreditTransfer creditTransfer = CreditTransferBuilder.newCreditTransfer().withFromBic(FROM_BIC)
				.withToBic(TO_BIC).withFromIban(FROM_IBAN).withToIban(TO_IBAN).withAmount(AMOUNT).withCurrency(CURRENCY)
				.withDebtorAgent(FROM_BIC).withCreditorAgent(TO_BIC).build();

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				sendPaymentResult.complete(invocation.getArguments());

				return null;
			}
		}).when(paymentServiceMock).sendPaymentAsync(any(), any(), any(), any(), any(), any(), any());

		channel.basicPublish("", "iso-broker/icf", null, new XmlMapper().writeValueAsBytes(creditTransfer));

		final Object[] result = sendPaymentResult.get(30, TimeUnit.SECONDS);

		assertEquals(bic(FROM_BIC), result[0]);
		assertEquals(bic(TO_BIC), result[1]);
		assertEquals(iban(FROM_IBAN), result[2]);
		assertEquals(iban(TO_IBAN), result[3]);
		assertEquals(Currency.valueOf(CURRENCY), result[4]);
		assertEquals(Currency.valueOf(CURRENCY).deDecimalise(AMOUNT), result[5]);

		final CompletableFuture<byte[]> result2 = new CompletableFuture<>();

		channel.basicConsume("iso-broker/scf", new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				result2.complete(body);
			}
		});

		paymentListener.paymentReceived(bic(FROM_BIC), bic(TO_BIC), iban(FROM_IBAN), iban(TO_IBAN),
				Currency.valueOf(CURRENCY), Currency.valueOf(CURRENCY).deDecimalise(AMOUNT), Collections.emptyMap());

		final CreditTransfer inboundCreditTransfer = new XmlMapper().readValue(result2.get(), CreditTransfer.class);

		assertEquals(FROM_BIC, inboundCreditTransfer.getDebtor().getId().getOrganisationId().getBicOrBei());
		assertEquals(TO_BIC, inboundCreditTransfer.getCreditor().getId().getOrganisationId().getBicOrBei());
		assertEquals(FROM_IBAN, inboundCreditTransfer.getDebtorAccount().getId().getIban());
		assertEquals(TO_IBAN, inboundCreditTransfer.getCreditorAccount().getId().getIban());
		assertEquals(CURRENCY, inboundCreditTransfer.getInterbankSettlementAmount().getCurrency());
		assertEquals(AMOUNT, inboundCreditTransfer.getInterbankSettlementAmount().getAmount());
	}

	@AfterClass
	public static void tearDown() {

	}
}