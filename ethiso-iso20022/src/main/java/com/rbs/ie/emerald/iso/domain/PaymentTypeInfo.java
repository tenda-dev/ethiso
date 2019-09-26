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

public class PaymentTypeInfo {
	private String instructionPriority;
	private CodeOrProprietary categoryPurpose;
	private ServiceLevel serviceLevel;
	private CodeOrProprietary localInstrument;

	@JsonProperty("InstrPrty")
	public String getInstructionPriority() {
		return instructionPriority;
	}

	public void setInstructionPriority(String instructionPriority) {
		this.instructionPriority = instructionPriority;
	}

	@JsonProperty("CtgyPurp")
	public CodeOrProprietary getCategoryPurpose() {
		return categoryPurpose;
	}
	
	public void setCategoryPurpose(CodeOrProprietary categoryPurpose) {
		this.categoryPurpose = categoryPurpose;
	}
	
	@JsonProperty("SvcLvl")
	public ServiceLevel getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(ServiceLevel serviceLevel) {
		this.serviceLevel = serviceLevel;
	}
	
	@JsonProperty("LclInstrm")
	public CodeOrProprietary getLocalInstrument() {
		return localInstrument;
	}
	
	public void setLocalInstrument(CodeOrProprietary localInstrument) {
		this.localInstrument = localInstrument;
	}
}