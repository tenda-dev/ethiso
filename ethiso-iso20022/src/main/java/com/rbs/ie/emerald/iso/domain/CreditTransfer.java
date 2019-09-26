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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class CreditTransfer {
	public static final String REMITTANCE_INFO_UNSTRUCTURED = "RmtInfUstrd";
	public static final String REMITTANCE_INFO_STRUCTURED = "RmtInfStrd";

	private PaymentId paymentId;
	private PaymentTypeInfo paymentTypeInfo;
	private Amount interbankSettlementAmount;
	private Date interbankSettlementDate;
	private Amount instructedAmount;
	private String chargeBearer;
	private Agent instructingAgent;
	private List<ChargesInfo> chargesInfo;
	private Party ultimateDebtor;
	private Party debtor;
	private Account debtorAccount;
	private Agent debtorAgent;
	private Party ultimateCreditor;
	private Party creditor;
	private Account creditorAccount;
	private Agent creditorAgent;
	private CodeOrProprietary categoryPurpose;
	private RemittanceInfo remittanceInfo;
	private String instuctionForCreditorAgent;

	@JsonProperty("PmtId")
	public PaymentId getPaymentId() {
		return paymentId;
	}
	
	public void setPaymentId(PaymentId paymentId) {
		this.paymentId = paymentId;
	}
	
	@JsonProperty("PmtTpInf")
	public PaymentTypeInfo getPaymentTypeInfo() {
		return paymentTypeInfo;
	}
	
	public void setPaymentTypeInfo(PaymentTypeInfo paymentTypeInfo) {
		this.paymentTypeInfo = paymentTypeInfo;
	}
	
	@JsonProperty("IntrBkSttlmAmt")
	public Amount getInterbankSettlementAmount() {
		return interbankSettlementAmount;
	}
	
	public void setInterbankSettlementAmount(Amount interbankSettlementAmount) {
		this.interbankSettlementAmount = interbankSettlementAmount;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	@JsonProperty("IntrBkSttlmDt")
	public Date getInterbankSettlementDate() {
		return interbankSettlementDate;
	}
	
	public void setInterbankSettlementDate(Date interbankSettlementDate) {
		this.interbankSettlementDate = interbankSettlementDate;
	}
	
	@JsonProperty("InstdAmt")
	public Amount getInstructedAmount() {
		return instructedAmount;
	}
	
	public void setInstructedAmount(Amount instructedAmount) {
		this.instructedAmount = instructedAmount;
	}
	
	@JsonProperty("ChrgBr")
	public String getChargeBearer() {
		return chargeBearer;
	}
	
	public void setChargeBearer(String chargeBearer) {
		this.chargeBearer = chargeBearer;
	}

	@JsonProperty("InstgAgt")
	public Agent getInstructingAgent() {
		return instructingAgent;
	}
	
	public void setInstructingAgent(Agent instructingAgent) {
		this.instructingAgent = instructingAgent;
	}
	
	@JacksonXmlElementWrapper(useWrapping = false)
	@JsonProperty("ChrgsInf")
	public List<ChargesInfo> getChargesInfo() {
		return chargesInfo;
	}
	
	public void setChargesInfo(List<ChargesInfo> chargesInfo) {
		this.chargesInfo = chargesInfo;
	}
	
	@JsonProperty("UltmtDbtr")
	public Party getUltimateDebtor() {
		return ultimateDebtor;
	}
	
	public void setUltimateDebtor(Party ultimateDebtor) {
		this.ultimateDebtor = ultimateDebtor;
	}
	
	@JsonProperty("Dbtr")
	public Party getDebtor() {
		return debtor;
	}
	
	public void setDebtor(Party debtor) {
		this.debtor = debtor;
	}
	
	@JsonProperty("DbtrAcct")
	public Account getDebtorAccount() {
		return debtorAccount;
	}
	
	public void setDebtorAccount(Account debtorAccount) {
		this.debtorAccount = debtorAccount;
	}
	
	@JsonProperty("DbtrAgt")
	public Agent getDebtorAgent() {
		return debtorAgent;
	}
	
	public void setDebtorAgent(Agent debtorAgent) {
		this.debtorAgent = debtorAgent;
	}

	@JsonProperty("UltmtCdtr")
	public Party getUltimateCreditor() {
		return ultimateCreditor;
	}
	
	public void setUltimateCreditor(Party ultimateCreditor) {
		this.ultimateCreditor = ultimateCreditor;
	}
	
	@JsonProperty("Cdtr")
	public Party getCreditor() {
		return creditor;
	}
	
	public void setCreditor(Party creditor) {
		this.creditor = creditor;
	}
	
	@JsonProperty("CdtrAcct")
	public Account getCreditorAccount() {
		return creditorAccount;
	}

	public void setCreditorAccount(Account creditorAccount) {
		this.creditorAccount = creditorAccount;
	}

	@JsonProperty("CdtrAgt")
	public Agent getCreditorAgent() {
		return creditorAgent;
	}
	
	public void setCreditorAgent(Agent creditorAgent) {
		this.creditorAgent = creditorAgent;
	}
	
	@JsonProperty("Purp")
	public CodeOrProprietary getCategoryPurpose() {
		return categoryPurpose;
	}
	
	public void setCategoryPurpose(CodeOrProprietary categoryPurpose) {
		this.categoryPurpose = categoryPurpose;
	}
	
	@JsonProperty("RmtInf")
	public RemittanceInfo getRemittanceInfo() {
		return remittanceInfo;
	}

	public void setRemittanceInfo(RemittanceInfo remittanceInfo) {
		this.remittanceInfo = remittanceInfo;
	}

	@JsonProperty("InstrForCdtrAgt")
	public String getInstuctionForCreditorAgent() {
		return instuctionForCreditorAgent;
	}
	
	public void setInstuctionForCreditorAgent(String instuctionForCreditorAgent) {
		this.instuctionForCreditorAgent = instuctionForCreditorAgent;
	}
	
	@JsonIgnore
	public Map<String, Object> getAdditionalProperties() {
		final Map<String, Object> additionalProperties = new HashMap<>();

		if (remittanceInfo != null) {

			if (remittanceInfo.getStructured() != null) {
				additionalProperties.put(REMITTANCE_INFO_STRUCTURED, remittanceInfo.getStructured());
			}

			if (remittanceInfo.getUnstructured() != null) {
				additionalProperties.put(REMITTANCE_INFO_UNSTRUCTURED, remittanceInfo.getUnstructured());
			}
		}

		return additionalProperties;
	}
}