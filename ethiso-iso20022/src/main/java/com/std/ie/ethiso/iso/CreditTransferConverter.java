package com.std.ie.ethiso.iso;

import static com.std.ie.ethiso.domain.Bic.bic;
import static com.std.ie.ethiso.domain.Iban.iban;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import com.std.ie.ethiso.domain.Currency;
import com.std.ie.ethiso.iso.domain.CreditTransfer;

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
public class CreditTransferConverter {

	public ethisoPayment convert(CreditTransfer creditTransfer) {
		final String from = creditTransfer.getDebtorAccount().getId().getIban();
		final String to = creditTransfer.getCreditorAccount().getId().getIban();

		//final String fromBic = creditTransfer.getDebtor().getId().getOrganisationId().getBicOrBei();
        //final String toBic = creditTransfer.getCreditor().getId().getOrganisationId().getBicOrBei();

        final String fromBic = creditTransfer.getDebtorAgent().getFinancialInstituionId().getBic();
        final String toBic = creditTransfer.getCreditorAgent().getFinancialInstituionId().getBic();

		final BigDecimal decimalAmount = creditTransfer.getInterbankSettlementAmount().getAmount();

		final Currency ccy = Currency.valueOf(creditTransfer.getInterbankSettlementAmount().getCurrency());

		final BigInteger amount = ccy.deDecimalise(decimalAmount);

		final Map<String, Object> additionalProperties = creditTransfer.getAdditionalProperties();

		return new ethisoPayment(bic(fromBic), bic(toBic), iban(from), iban(to), ccy, amount, additionalProperties);
	}
}