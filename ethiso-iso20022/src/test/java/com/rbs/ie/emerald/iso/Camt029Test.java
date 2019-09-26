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

package com.rbs.ie.emerald.iso;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.rbs.ie.emerald.iso.domain.Assignment;
import com.rbs.ie.emerald.iso.domain.Camt029;
import com.rbs.ie.emerald.iso.domain.TransactionInformationAndStatus;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class Camt029Test {
	private static final XmlMapper XML_MAPPER = new XmlMapper();


	@Test
	public void templateLoads() throws IOException {
		XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		XML_MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		final Camt029 camt029 = map("/camt.029.test.xml");
		final Assignment assignment = camt029.getResolutionOfInvestigation().getAssignment();
		final TransactionInformationAndStatus transactionInformationAndStatus =
				camt029.getResolutionOfInvestigation()
						.getCancellationDetails().getTransactionInformationAndStatus();

		assertEquals("p:Id", assignment.getId());
		assertEquals("p:FinInstID2", assignment.getAssignee().getAgent()
				.getFinancialInstituionId().getBic());
		assertEquals("p:FinInstID1", assignment.getAssigner().getAgent()
				.getFinancialInstituionId().getBic());
		assertEquals("CreDtTm", "2001-12-31T10:15:20", df.format(assignment.getCreationDateTime()));

		assertEquals("CNCL", camt029.getResolutionOfInvestigation()
				.getInvestigationStatus().getConfirmation());

		assertEquals("p:OrgnlMsgNmId", transactionInformationAndStatus
				.getOriginalGroupInformation().getOriginalMessageName());

		assertEquals("p:Cd", transactionInformationAndStatus
				.getOriginalTransactionReference().getPaymentTypeInfo()
				.getServiceLevel().getCode());

		assertEquals("p:AnyBIC", transactionInformationAndStatus
				.getCancellationStatusReasonInformation().getOriginator()
				.getIdentification().getOrganisationIdentification().getBICorBEI());
		assertEquals("ABCD", transactionInformationAndStatus
				.getCancellationStatusReasonInformation().getReason().getProprietary());

		assertEquals("p:Nm", transactionInformationAndStatus.getOriginalTransactionReference()
		.getDebtor().getName());
		assertEquals("p:Id", transactionInformationAndStatus.getOriginalTransactionReference()
		.getDebtor().getId().getOrganisationId().getOthers().get(0).getId());

		assertEquals("p:BICF-CdtrAgt", transactionInformationAndStatus.getOriginalTransactionReference()
				.getCreditorAgent().getFinancialInstituionId().getBic());
		assertEquals("p:BICF-DbtrAgt", transactionInformationAndStatus.getOriginalTransactionReference()
				.getDebtorAgent().getFinancialInstituionId().getBic());

		assertEquals("p:Ctry", transactionInformationAndStatus.getOriginalTransactionReference()
		.getCreditor().getPostalAddress().getCountry());

		assertEquals("p:IBAN4", transactionInformationAndStatus.getOriginalTransactionReference()
		.getCreditorAccount().getId().getIban());

	}


	private Camt029 map(String filename) throws IOException {
		return XML_MAPPER.readValue(Camt029Test.class.getResourceAsStream(filename), Camt029.class);
	}
}
