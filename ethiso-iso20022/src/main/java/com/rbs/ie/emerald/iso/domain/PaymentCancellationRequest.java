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

public class PaymentCancellationRequest {

	private Assignment assignment;
	private ControlData controlData;
	private UnderlyingTransaction underlyingTransaction;

	@JsonProperty("Assgnmt")
	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	@JsonProperty("CtrlData")
	public ControlData getControlData() {
		return controlData;
	}

	public void setControlData(ControlData controlData) {
		this.controlData = controlData;
	}

	@JsonProperty("Undrlyg")
	public UnderlyingTransaction getUnderlyingTransaction() {
		return underlyingTransaction;
	}

	public void setUnderlyingTransaction(UnderlyingTransaction underlyingTransaction) {
		this.underlyingTransaction = underlyingTransaction;
	}
}
