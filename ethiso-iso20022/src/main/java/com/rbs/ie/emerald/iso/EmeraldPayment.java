package com.rbs.ie.ethiso.iso;

import java.math.BigInteger;
import java.util.Map;

import com.rbs.ie.ethiso.domain.Bic;
import com.rbs.ie.ethiso.domain.Currency;
import com.rbs.ie.ethiso.domain.Iban;

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
public class ethisoPayment {

	private Bic fromBic;
	private Bic toBic;
	private Iban fromIban;
	private Iban toIban;
	private Currency currency;
	private BigInteger amount;
	private Map<String, Object> additionalProperties;

	public ethisoPayment(Bic fromBic, Bic toBic, Iban fromIban, Iban toIban, Currency currency, BigInteger amount,
			Map<String, Object> additionalProperties) {
		this.fromBic = fromBic;
		this.toBic = toBic;
		this.fromIban = fromIban;
		this.toIban = toIban;
		this.currency = currency;
		this.amount = amount;
		this.additionalProperties = additionalProperties;
	}

	public Iban getFromIban() {
		return fromIban;
	}

	public Iban getToIban() {
		return toIban;
	}

	public Bic getFromBic() {
		return fromBic;
	}

	public Bic getToBic() {
		return toBic;
	}

	public Currency getCurrency() {
		return currency;
	}

	public BigInteger getAmount() {
		return amount;
	}

	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	@Override
	public String toString() {
		return "ethisoPayment{" + "fromBic=" + fromBic + ", toBic=" + toBic + ", fromIban=" + fromIban + ", toIban="
				+ toIban + ", currency=" + currency + ", amount=" + amount + ", additionalProperties="
				+ additionalProperties + '}';
	}
}