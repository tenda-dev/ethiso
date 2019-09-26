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

public class Account {
	private AccountId id;

	@JsonProperty("Id")
	public AccountId getId() {
		return id;
	}

	public void setId(AccountId id) {
		this.id = id;
	}

	// serialization
	public Account() {
	}

	@Override
	public String toString() {
		return "Account{" +
				"id=" + id +
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

		Account account = (Account) o;

		return id != null ? id.equals(account.id) : account.id == null;

	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}