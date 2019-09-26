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
package com.std.ie.ethiso.iso.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OriginalTransactionReference extends CreditTransfer {

	private SettlementInfo settlementInformation;
	private AgentAccount CreditorAgentAccount;

	@JsonProperty("SttlmInf")
	public SettlementInfo getSettlementInformation() {
		return settlementInformation;
	}

	public void setSettlementInformation(SettlementInfo settlementInformation) {
		this.settlementInformation = settlementInformation;
	}

	@JsonProperty("CdtrAgtAcct")
	public AgentAccount getCreditorAgentAccount() {
		return CreditorAgentAccount;
	}

	public void setCreditorAgentAccount(AgentAccount creditorAgentAccount) {
		CreditorAgentAccount = creditorAgentAccount;
	}
}
