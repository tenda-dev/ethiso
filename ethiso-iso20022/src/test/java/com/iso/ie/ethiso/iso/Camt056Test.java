package com.iso.ie.ethiso.iso;

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


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.iso.ie.ethiso.iso.domain.Assignment;
import com.iso.ie.ethiso.iso.domain.Camt056;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import com.iso.ie.ethiso.iso.domain.TransactionInformationAndStatus;

public class Camt056Test {
	private static final XmlMapper XML_MAPPER = new XmlMapper();

	@Test
	public void templateLoads() throws IOException {
		XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		XML_MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		final Camt056 camt056 = map("/camt.056.test.xml");
		final Assignment assignment = camt056.getPaymentCancellationRequest().getAssignment();
		final TransactionInformationAndStatus transactionInformationAndStatus
				= camt056.getPaymentCancellationRequest().getUnderlyingTransaction()
				.getTransactionInformationAndStatus();

		assertEquals("p:Id", assignment.getId());
		assertEquals("p:bic2-assignee", assignment.getAssignee().getAgent()
				.getFinancialInstituionId().getBic());
		assertEquals("p:bic1-assignor", assignment.getAssigner().getAgent()
				.getFinancialInstituionId().getBic());
		
		
		System.out.println(assignment.getCreationDateTime().getTime());
		
		long expectedTime = 1007122394000L;
		
		assertEquals("CreDtTm", expectedTime, assignment.getCreationDateTime().getTime());
		
		//assertEquals("CreDtTm", "2001-11-30T12:13:14", dateTimeFormat.format(assignment.getCreationDateTime()));

		assertEquals(999, camt056.getPaymentCancellationRequest().getControlData().getNumberOfTransactions());


		assertEquals("p:CxlId", transactionInformationAndStatus
				.getCancellationIdentification());

		assertEquals("DUPL", transactionInformationAndStatus.getCancellationReasonInformation()
				.getReason().getCode());


		assertEquals("2001-01-01", dateFormat.format(transactionInformationAndStatus
				.getOriginalInterbankSettlementDate()));
		assertEquals("CCY", transactionInformationAndStatus
				.getOriginalInterbankSettlementAmount().getCurrency());
		assertEquals(new BigDecimal("123.45"), transactionInformationAndStatus
				.getOriginalInterbankSettlementAmount().getAmount());

		assertEquals("cancel-reason-name", transactionInformationAndStatus
				.getCancellationReasonInformation().getOriginator()
				.getName());

		assertEquals("DUPL", transactionInformationAndStatus
				.getCancellationReasonInformation().getReason().getCode());


		assertEquals("cancel-reason-name", transactionInformationAndStatus
				.getCancellationReasonInformation().getOriginator().getName());


		assertEquals("settle-info-cd", transactionInformationAndStatus
				.getOriginalTransactionReference().getSettlementInformation()
				.getClearingSystem().getCode());

		assertEquals("CreditorAgentIBAN", transactionInformationAndStatus
				.getOriginalTransactionReference().getCreditorAgentAccount()
				.getAccountid().getIban());

		assertEquals("INDA", transactionInformationAndStatus.
				getOriginalTransactionReference().getSettlementInformation()
				.getSettlementMethod());

		assertEquals("CreditorAgentBIC", transactionInformationAndStatus
				.getOriginalTransactionReference().getCreditorAgent()
				.getFinancialInstituionId().getBic());

		assertEquals("TxInfAssignerBIC", transactionInformationAndStatus.getAssigner()
				.getFinancialInstituionId().getBic());

	}
	
	@Test
	public void isBrazillianGood() throws IOException {
		XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		XML_MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		TimeZone defaultTimeZone = TimeZone.getDefault();
		TimeZone.setDefault(TimeZone.getTimeZone("Brazil/East"));
		
		DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		final Camt056 camt056 = map("/camt.056.test.xml");
		final Assignment assignment = camt056.getPaymentCancellationRequest().getAssignment();
		
		System.out.println(assignment.getCreationDateTime().getTime());
		
		long expectedTime = 1007122394000L;
		
		System.out.println(assignment.getCreationDateTime());
		
		assertEquals("CreDtTm", expectedTime, assignment.getCreationDateTime().getTime());
		
		TimeZone.setDefault(defaultTimeZone);
	}


	private Camt056 map(String filename) throws IOException {
		return XML_MAPPER.readValue(Camt056Test.class.getResource(filename), Camt056.class);
	}
}
