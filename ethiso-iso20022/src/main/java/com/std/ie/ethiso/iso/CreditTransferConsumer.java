package com.std.ie.ethiso.iso;

import static com.std.ie.ethiso.iso.Iso20022Broker.EXCHANGE;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.std.ie.ethiso.PaymentService;
import com.std.ie.ethiso.iso.domain.CodeOrProprietary;
import com.std.ie.ethiso.iso.domain.CreditTransfer;
import com.std.ie.ethiso.iso.domain.CreditTransferStatus;
import com.std.ie.ethiso.iso.domain.PaymentId;
import com.std.ie.ethiso.iso.domain.Status;
import com.std.ie.ethiso.iso.domain.StatusReasonInfo;

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
public class CreditTransferConsumer extends DefaultConsumer {

	private static final Logger LOGGER = getLogger(CreditTransferConsumer.class);

	private static final String BIC_NOT_SEPA_REACHABLE = "XT27";

	private final PaymentService paymentService;
	private final String statusRoutingKey;
	private final XmlMapper xmlMapper = new XmlMapper();
	private final CreditTransferConverter converter = new CreditTransferConverter();

	public CreditTransferConsumer(PaymentService paymentService, Channel channel, final String statusRoutingKey) {
		super(channel);
		this.paymentService = paymentService;
		this.statusRoutingKey = statusRoutingKey;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
		try {
			final CreditTransfer creditTransfer = getCreditTransfer(body);

			final ethisoPayment payment = converter.convert(creditTransfer);

			LOGGER.info("Sending " + payment);

			final CreditTransferStatus creditTransferStatus = new CreditTransferStatus();

			final PaymentId paymentId = creditTransfer.getPaymentId();

			creditTransferStatus.setStatusId(paymentId.getTransactionId());
			creditTransferStatus.setOriginalInstructionId(paymentId.getInstructionId());
			creditTransferStatus.setOriginalEndToEndId(paymentId.getEndToEndId());
			creditTransferStatus.setOriginalTransactionId(paymentId.getTransactionId());

			final StatusReasonInfo statusReasonInfo = new StatusReasonInfo();

			statusReasonInfo.setOriginator(creditTransfer.getDebtor());

			creditTransferStatus.setStatusReasonInfo(statusReasonInfo);

			final CompletableFuture<Void> future = (CompletableFuture<Void>) paymentService.sendPaymentAsync(
					payment.getFromBic(), payment.getToBic(), payment.getFromIban(), payment.getToIban(),
					payment.getCurrency(), payment.getAmount(), payment.getAdditionalProperties());

			future.handle((v, t) -> {
				if (t == null) {
					creditTransferStatus.setTransactionStatus(Status.ACSP);
				} else {
					LOGGER.error("Payment error [" + t + "]");

					creditTransferStatus.setTransactionStatus(Status.RJCT);

					final CodeOrProprietary reasonCode = new CodeOrProprietary();

					// TODO: Set a sensible reasonCode here ...

					reasonCode.setCode(BIC_NOT_SEPA_REACHABLE);

					statusReasonInfo.setReason(reasonCode);
				}

				try {
					getChannel().basicPublish(EXCHANGE, statusRoutingKey, null,
							xmlMapper.writeValueAsBytes(creditTransferStatus));
				} catch (final Exception e) {
					LOGGER.error("Failed to dispatch pacs002 [" + e + "]");
				}

				return null;
			});
		} catch (final RuntimeException e) {
			LOGGER.error("Caught " + e + " processing " + new String(body, 0, Math.min(100, body.length)), e);
		}
	}

	private CreditTransfer getCreditTransfer(final byte[] body) throws IOException {
		return xmlMapper.readValue(body, CreditTransfer.class);
	}
}