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

package com.iso.ie.ethiso.iso.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CancellationStatusReasonInformation {
	private Originator originator;
	private CodeOrProprietary reason;

	@JsonProperty("Orgtr")
	public Originator getOriginator() {
		return originator;
	}

	public void setOriginator(Originator originator) {
		this.originator = originator;
	}

	@JsonProperty("Rsn")
	public CodeOrProprietary getReason() {
		return reason;
	}

	public void setReason(CodeOrProprietary reason) {
		this.reason = reason;
	}

}
