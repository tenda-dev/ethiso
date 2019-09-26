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
package com.rbs.ie.ethiso.ethereum.rest;

import java.math.BigDecimal;

import com.rbs.ie.ethiso.domain.Bic;
import com.rbs.ie.ethiso.domain.Currency;
import com.rbs.ie.ethiso.domain.Iban;

public class PaymentBody {
	private Bic fromBic;
	private Bic toBic;
	private Iban fromIban;
	private Iban toIban;
	private Currency ccy;
	private BigDecimal amount;
	private String remittanceInfo;

	public Bic getFromBic() {
		return fromBic;
	}

	public void setFromBic(Bic fromBic) {
		this.fromBic = fromBic;
	}

	public Bic getToBic() {
		return toBic;
	}

	public void setToBic(Bic toBic) {
		this.toBic = toBic;
	}

	public Iban getFromIban() {
		return fromIban;
	}

	public void setFromIban(Iban fromIban) {
		this.fromIban = fromIban;
	}

	public Iban getToIban() {
		return toIban;
	}

	public void setToIban(Iban toIban) {
		this.toIban = toIban;
	}

	public Currency getCcy() {
		return ccy;
	}

	public void setCcy(Currency ccy) {
		this.ccy = ccy;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getRemittanceInfo() {
		return remittanceInfo;
	}
	
	public void setRemittanceInfo(String remittanceInfo) {
		this.remittanceInfo = remittanceInfo;
	}
}