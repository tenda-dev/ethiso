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

public class ResolutionOfInvestigation {
	private Assignment assignment;
	private InvestigationStatus investigationStatus;
	private CancellationDetails cancellationDetails;

	@JsonProperty("Assgnmt")
	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	@JsonProperty("Sts")
	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	@JsonProperty("CxlDtls")
	public CancellationDetails getCancellationDetails() {
		return cancellationDetails;
	}

	public void setCancellationDetails(CancellationDetails cancellationDetails) {
		this.cancellationDetails = cancellationDetails;
	}
}