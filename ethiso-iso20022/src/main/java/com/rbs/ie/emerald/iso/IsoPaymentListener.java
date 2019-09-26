package com.rbs.ie.emerald.iso;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.rabbitmq.client.Channel;
import com.rbs.ie.emerald.PaymentListener;
import com.rbs.ie.emerald.domain.Bic;
import com.rbs.ie.emerald.domain.Currency;
import com.rbs.ie.emerald.domain.Iban;
import com.rbs.ie.emerald.iso.domain.Account;
import com.rbs.ie.emerald.iso.domain.AccountId;
import com.rbs.ie.emerald.iso.domain.Amount;
import com.rbs.ie.emerald.iso.domain.CreditTransfer;
import com.rbs.ie.emerald.iso.domain.EntityId;
import com.rbs.ie.emerald.iso.domain.Party;
import com.rbs.ie.emerald.iso.domain.PartyId;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

import static com.rbs.ie.emerald.iso.Iso20022Broker.EXCHANGE;
import static org.slf4j.LoggerFactory.getLogger;


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
public class IsoPaymentListener implements PaymentListener {


	private static final Logger LOGGER = getLogger(IsoPaymentListener.class);

	private Channel channel;
	private String routingKey;
	private final XmlMapper xmlMapper = new XmlMapper();

	public IsoPaymentListener(Channel channel, String routingKey) {
		this.channel = channel;
		this.routingKey = routingKey;
	}

	@Override
	public void paymentReceived(final Bic fromBic, final Bic toBic, final Iban from, final Iban to, final Currency currency, final BigInteger paymentAmount,
								final Map<String, Object> additionalProperties) {
		// Received ingIban, GBP, 1, {RmtInfUstrd=this is reference 1458}
		LOGGER.info("tk=end,from={},to={},amt={},ccy={},ref={}", from, to, paymentAmount.intValue(), currency,
				additionalProperties.get("RmtInfUstrd"));

		// Amount is scaled per currency - must convert back

		final AccountId fromAccountId = new AccountId();

		fromAccountId.setIban(from.toString());

		final Account fromAccount = new Account();

		fromAccount.setId(fromAccountId);

		final AccountId toAccountId = new AccountId();

		toAccountId.setIban(to.toString());

		final Account toAccount = new Account();

		toAccount.setId(toAccountId);

		final Amount settlementAmount = new Amount();

		settlementAmount.setAmount(currency.decimalise(paymentAmount));
		settlementAmount.setCurrency(currency.toString());

		final CreditTransfer creditTransfer = new CreditTransfer();

		creditTransfer.setCreditorAccount(toAccount);
		creditTransfer.setDebtorAccount(fromAccount);
		creditTransfer.setInterbankSettlementAmount(settlementAmount);

		creditTransfer.setCreditor(createPartyWith(toBic));
		creditTransfer.setDebtor(createPartyWith(fromBic));

		try {
			channel.basicPublish(EXCHANGE, routingKey, null, xmlMapper.writeValueAsBytes(creditTransfer));
		} catch (final IOException e) {
			LOGGER.warn("Failed to send confirmation [" + e + "]");
		}
	}

	private Party createPartyWith(Bic toBic) {
		EntityId entityId = new EntityId();
		entityId.setBicOrBei(toBic.toString());

		PartyId partyid = new PartyId();
		partyid.setOrganisationId(entityId);

		Party party = new Party();
		party.setId(partyid);
		return party;
	}

}