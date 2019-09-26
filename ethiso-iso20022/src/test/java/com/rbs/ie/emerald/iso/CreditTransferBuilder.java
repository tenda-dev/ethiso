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
package com.rbs.ie.ethiso.iso;

import java.math.BigDecimal;

import com.rbs.ie.ethiso.iso.domain.Account;
import com.rbs.ie.ethiso.iso.domain.AccountId;
import com.rbs.ie.ethiso.iso.domain.Agent;
import com.rbs.ie.ethiso.iso.domain.Amount;
import com.rbs.ie.ethiso.iso.domain.CreditTransfer;
import com.rbs.ie.ethiso.iso.domain.EntityId;
import com.rbs.ie.ethiso.iso.domain.FinancialInstituionId;
import com.rbs.ie.ethiso.iso.domain.Party;
import com.rbs.ie.ethiso.iso.domain.PartyId;
import com.rbs.ie.ethiso.iso.domain.PaymentId;

public class CreditTransferBuilder {
	private CreditTransfer creditTransfer = new CreditTransfer();

	private CreditTransferBuilder() {
		creditTransfer.setPaymentId(new PaymentId());
		creditTransfer.setInterbankSettlementAmount(new Amount());
	}

	public static CreditTransferBuilder newCreditTransfer() {
		return new CreditTransferBuilder();
	}

	public CreditTransferBuilder withFromBic(final String bic) {
		creditTransfer.setDebtor(createParty(bic));

		return this;
	}

	public CreditTransferBuilder withToBic(final String bic) {
		creditTransfer.setCreditor(createParty(bic));

		return this;
	}

	public CreditTransferBuilder withFromIban(final String iban) {
		creditTransfer.setDebtorAccount(createAccount(iban));

		return this;
	}	
	
	public CreditTransferBuilder withCreditorAgent(final String bic)
	{		
		FinancialInstituionId financialInstituionId = new FinancialInstituionId();
		financialInstituionId.setBic(bic);
		Agent creditorAgent = new Agent();
		creditorAgent.setFinancialInstituionId(financialInstituionId);
		creditTransfer.setCreditorAgent(creditorAgent);
		return this;
	}
	
	public CreditTransferBuilder withDebtorAgent(final String bic)
	{		
		FinancialInstituionId financialInstituionId = new FinancialInstituionId();
		financialInstituionId.setBic(bic);
		Agent debtorAgent = new Agent();
		debtorAgent.setFinancialInstituionId(financialInstituionId);
		creditTransfer.setDebtorAgent(debtorAgent);
		return this;
	}
	

	public CreditTransferBuilder withToIban(final String iban) {
		creditTransfer.setCreditorAccount(createAccount(iban));

		return this;
	}

	public CreditTransferBuilder withAmount(final BigDecimal amount) {
		creditTransfer.getInterbankSettlementAmount().setAmount(amount);

		return this;
	}

	public CreditTransferBuilder withCurrency(final String currency) {
		creditTransfer.getInterbankSettlementAmount().setCurrency(currency);

		return this;
	}

	public CreditTransfer build() {
		return creditTransfer;
	}

	private static Party createParty(final String iban) {
		final EntityId entityId = new EntityId();

		entityId.setBicOrBei(iban);

		final PartyId partyId = new PartyId();

		partyId.setOrganisationId(entityId);

		final Party party = new Party();

		party.setId(partyId);

		return party;
	}

	private static Account createAccount(final String iban) {
		final AccountId accountId = new AccountId();

		accountId.setIban(iban);

		final Account account = new Account();

		account.setId(accountId);

		return account;
	}

}
