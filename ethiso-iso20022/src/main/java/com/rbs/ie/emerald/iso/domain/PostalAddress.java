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

public class PostalAddress {
	private List<String> addressLines;
	private String streetName;
	private String buildingNumber;
	private String postCode;
	private String townName;
	private String country;

	@JacksonXmlElementWrapper(useWrapping = false)
	@JsonProperty("AdrLine")
	public List<String> getAddressLines() {
		return addressLines;
	}
	
	public void setAddressLines(List<String> addressLines) {
		this.addressLines = addressLines;
	}
	
	@JsonProperty("StrtNm")
	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	@JsonProperty("BldgNb")
	public String getBuildingNumber() {
		return buildingNumber;
	}

	public void setBuildingNumber(String buildingNumber) {
		this.buildingNumber = buildingNumber;
	}

	@JsonProperty("PstCd")
	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	@JsonProperty("TwnNm")
	public String getTownName() {
		return townName;
	}

	public void setTownName(String townName) {
		this.townName = townName;
	}

	@JsonProperty("Ctry")
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}