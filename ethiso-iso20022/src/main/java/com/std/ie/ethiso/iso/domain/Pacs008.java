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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Document", namespace = "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.01")
public class Pacs008 {
	private CreditTransferList creditTransferList;

	@JsonProperty("pacs.008.001.01")
	public CreditTransferList getCreditTransferList() {
		return creditTransferList;
	}

	public void setCreditTransferList(CreditTransferList creditTransferList) {
		this.creditTransferList = creditTransferList;
	}
}