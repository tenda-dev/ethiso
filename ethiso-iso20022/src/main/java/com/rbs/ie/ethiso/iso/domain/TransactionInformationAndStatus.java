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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class TransactionInformationAndStatus {
	private String cancellationStatusIdentification;
	private OriginalGroupInformation originalGroupInformation;
	private String OriginalMessageIdentification;
	private String OriginalInstructionIdentification;
	private String OriginalEndToEndIdentification;
	private String OriginalTransactionIdentification;
	private String TransactionCancellationStatus;
	private CancellationStatusReasonInformation cancellationStatusReasonInformation;
	private OriginalTransactionReference originalTransactionReference;

	private String cancellationIdentification;
	private CancellationStatusReasonInformation cancellationReasonInformation;
	private Amount originalInterbankSettlementAmount;
	private Date originalInterbankSettlementDate;
	private PartyAgent assigner;

	@JsonProperty("CxlStsId")
	public String getCancellationStatusIdentification() {
		return cancellationStatusIdentification;
	}

	public void setCancellationStatusIdentification(String cancellationStatusIdentification) {
		this.cancellationStatusIdentification = cancellationStatusIdentification;
	}

	@JsonProperty("OrgnlGrpInf")
	public OriginalGroupInformation getOriginalGroupInformation() {
		return originalGroupInformation;
	}

	public void setOriginalGroupInformation(OriginalGroupInformation originalGroupInformation) {
		this.originalGroupInformation = originalGroupInformation;
	}

	@JsonProperty("OrgnlMsgId")
	public String getOriginalMessageIdentification() {
		return OriginalMessageIdentification;
	}

	public void setOriginalMessageIdentification(String originalMessageIdentification) {
		OriginalMessageIdentification = originalMessageIdentification;
	}

	@JsonProperty("OrgnlInstrId")
	public String getOriginalInstructionIdentification() {
		return OriginalInstructionIdentification;
	}

	public void setOriginalInstructionIdentification(String originalInstructionIdentification) {
		OriginalInstructionIdentification = originalInstructionIdentification;
	}

	@JsonProperty("OrgnlEndToEndId")
	public String getOriginalEndToEndIdentification() {
		return OriginalEndToEndIdentification;
	}

	public void setOriginalEndToEndIdentification(String originalEndToEndIdentification) {
		OriginalEndToEndIdentification = originalEndToEndIdentification;
	}

	@JsonProperty("OrgnlTxId")
	public String getOriginalTransactionIdentification() {
		return OriginalTransactionIdentification;
	}

	public void setOriginalTransactionIdentification(String originalTransactionIdentification) {
		OriginalTransactionIdentification = originalTransactionIdentification;
	}

	@JsonProperty("TxCxlSts")
	public String getTransactionCancellationStatus() {
		return TransactionCancellationStatus;
	}

	public void setTransactionCancellationStatus(String transactionCancellationStatus) {
		TransactionCancellationStatus = transactionCancellationStatus;
	}

	@JsonProperty("CxlStsRsnInf")
	public CancellationStatusReasonInformation getCancellationStatusReasonInformation() {
		return cancellationStatusReasonInformation;
	}

	public void setCancellationStatusReasonInformation(CancellationStatusReasonInformation cancellationStatusReasonInformation) {
		this.cancellationStatusReasonInformation = cancellationStatusReasonInformation;
	}

	@JsonProperty("OrgnlTxRef")
	public OriginalTransactionReference getOriginalTransactionReference() {
		return originalTransactionReference;
	}

	public void setOriginalTransactionReference(OriginalTransactionReference originalTransactionReference) {
		this.originalTransactionReference = originalTransactionReference;
	}

	@JsonProperty("CxlId")
	public String getCancellationIdentification() {
		return cancellationIdentification;
	}

	public void setCancellationIdentification(String cancellationIdentification) {
		this.cancellationIdentification = cancellationIdentification;
	}

	@JsonProperty("CxlRsnInf")
	public CancellationStatusReasonInformation getCancellationReasonInformation() {
		return cancellationReasonInformation;
	}

	public void setCancellationReasonInformation(CancellationStatusReasonInformation cancellationReasonInformation) {
		this.cancellationReasonInformation = cancellationReasonInformation;
	}

	@JsonProperty("OrgnlIntrBkSttlmAmt")
	public Amount getOriginalInterbankSettlementAmount() {
		return originalInterbankSettlementAmount;
	}

	public void setOriginalInterbankSettlementAmount(Amount originalInterbankSettlementAmount) {
		this.originalInterbankSettlementAmount = originalInterbankSettlementAmount;
	}


	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("OrgnlIntrBkSttlmDt")
	public Date getOriginalInterbankSettlementDate() {
		return originalInterbankSettlementDate;
	}

	public void setOriginalInterbankSettlementDate(Date originalInterbankSettlementDate) {
		this.originalInterbankSettlementDate = originalInterbankSettlementDate;
	}

	@JsonProperty("Assgnr")
	public PartyAgent getAssigner() {
		return assigner;
	}

	public void setAssigner(PartyAgent assigner) {
		this.assigner = assigner;
	}
}
