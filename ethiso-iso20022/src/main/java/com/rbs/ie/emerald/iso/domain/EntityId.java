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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class EntityId {
	private String bicOrBei;
	private DateAndPlaceOfBirth dateAndPlaceOfBirth;
	private List<Id> others;

	@JsonProperty("BICOrBEI")
	public String getBicOrBei() {
		return bicOrBei;
	}

	public void setBicOrBei(String bicOrBei) {
		this.bicOrBei = bicOrBei;
	}

	@JsonProperty("DtAndPlcOfBirth")
	public DateAndPlaceOfBirth getDateAndPlaceOfBirth() {
		return dateAndPlaceOfBirth;
	}

	public void setDateAndPlaceOfBirth(DateAndPlaceOfBirth dateAndPlaceOfBirth) {
		this.dateAndPlaceOfBirth = dateAndPlaceOfBirth;
	}

	@JsonProperty("Othr")
	@JacksonXmlElementWrapper(useWrapping = false)
	public List<Id> getOthers() {
		return others;
	}

	public void setOthers(List<Id> others) {
		this.others = others;
	}
}