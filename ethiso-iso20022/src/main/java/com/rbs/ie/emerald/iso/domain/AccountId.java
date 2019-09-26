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
package com.rbs.ie.ethiso.iso.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountId {
	private String iban;
	private ProprietaryAccount proprietaryAccount;

	@JsonProperty("IBAN")
	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	@JsonProperty("PrtryAcct")
	public ProprietaryAccount getProprietaryAccount() {
		return proprietaryAccount;
	}

	public void setProprietaryAccount(ProprietaryAccount proprietaryAccount) {
		this.proprietaryAccount = proprietaryAccount;
	}

	@Override
	public String toString() {
		return "AccountId{" +
				"iban='" + iban + '\'' +
				", proprietaryAccount=" + proprietaryAccount +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AccountId accountId = (AccountId) o;

		if (iban != null ? !iban.equals(accountId.iban) : accountId.iban != null) {
			return false;
		}
		return proprietaryAccount != null ? proprietaryAccount.equals(accountId.proprietaryAccount) : accountId.proprietaryAccount == null;

	}

	@Override
	public int hashCode() {
		int result = iban != null ? iban.hashCode() : 0;
		result = 31 * result + (proprietaryAccount != null ? proprietaryAccount.hashCode() : 0);
		return result;
	}
}