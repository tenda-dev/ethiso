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

public class CreditTransferStatus {
	private String statusId;
	private String originalInstructionId;
	private String originalEndToEndId;
	private String originalTransactionId;
	private Status transactionStatus;
	private StatusReasonInfo statusReasonInfo;

	@JsonProperty("StsId")
	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	@JsonProperty("OrgnlInstrId")
	public String getOriginalInstructionId() {
		return originalInstructionId;
	}

	public void setOriginalInstructionId(String originalInstructionId) {
		this.originalInstructionId = originalInstructionId;
	}

	@JsonProperty("OrgnlEndToEndId")
	public String getOriginalEndToEndId() {
		return originalEndToEndId;
	}

	public void setOriginalEndToEndId(String originalEndToEndId) {
		this.originalEndToEndId = originalEndToEndId;
	}

	@JsonProperty("OrgnlTxId")
	public String getOriginalTransactionId() {
		return originalTransactionId;
	}

	public void setOriginalTransactionId(String originalTransactionId) {
		this.originalTransactionId = originalTransactionId;
	}

	@JsonProperty("TxSts")
	public Status getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(Status transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	@JsonProperty("StsRsnInf")
	public StatusReasonInfo getStatusReasonInfo() {
		return statusReasonInfo;
	}

	public void setStatusReasonInfo(StatusReasonInfo statusReasonInfo) {
		this.statusReasonInfo = statusReasonInfo;
	}
}