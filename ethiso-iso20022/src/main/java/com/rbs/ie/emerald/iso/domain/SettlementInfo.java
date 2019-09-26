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
package com.rbs.ie.emerald.iso.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettlementInfo {
	private String settlementMethod;
	private Account settlementAccount;
	private ClearingSystem clearingSystem;
	private Agent instructingReimbursementAgent;
	private Agent instructedReimbursementAgent;

	@JsonProperty("SttlmMtd")
	public String getSettlementMethod() {
		return settlementMethod;
	}

	public void setSettlementMethod(String settlementMethod) {
		this.settlementMethod = settlementMethod;
	}

	@JsonProperty("SttlmAcct")
	public Account getSettlementAccount() {
		return settlementAccount;
	}
	
	public void setSettlementAccount(Account settlementAccount) {
		this.settlementAccount = settlementAccount;
	}
	
	@JsonProperty("ClrSys")
	public ClearingSystem getClearingSystem() {
		return clearingSystem;
	}
	
	public void setClearingSystem(ClearingSystem clearingSystem) {
		this.clearingSystem = clearingSystem;
	}
	
	@JsonProperty("InstgRmbrsmntAgt")
	public Agent getInstructingReimbursementAgent() {
		return instructingReimbursementAgent;
	}

	public void setInstructingReimbursementAgent(Agent instructingReimbursementAgent) {
		this.instructingReimbursementAgent = instructingReimbursementAgent;
	}

	@JsonProperty("InstdRmbrsmntAgt")
	public Agent getInstructedReimbursementAgent() {
		return instructedReimbursementAgent;
	}

	public void setInstructedReimbursementAgent(Agent instructedReimbursementAgent) {
		this.instructedReimbursementAgent = instructedReimbursementAgent;
	}
}