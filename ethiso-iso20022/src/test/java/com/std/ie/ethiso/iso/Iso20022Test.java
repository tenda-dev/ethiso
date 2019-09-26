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
package com.std.ie.ethiso.iso;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import com.std.ie.ethiso.iso.domain.CreditTransfer;
import org.junit.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.std.ie.ethiso.iso.domain.Pacs008;

public class Iso20022Test {
    private static final XmlMapper XML_MAPPER = new XmlMapper();

    @Test
    public void deserialiseEx1Step1() throws IOException {
        final Pacs008 pacs008 = map("/pacs.008.001.01_BusEx1_step1.xml");

        assertEquals(1, pacs008.getCreditTransferList().getGroupHeader().getNumberOfTransactions());
        List<CreditTransfer> transfers = pacs008.getCreditTransferList().getCreditTransfers();
        assertEquals(1, transfers.size());
    }

    @Test
    public void deserialiseEx1Step2() throws IOException {
        assertTransactionCount("/pacs.008.001.01_BusEx1_step2.xml", 2);
    }

    @Test
    public void deserialiseEx1Step3() throws IOException {
        assertTransactionCount("/pacs.008.001.01_BusEx1_step3.xml", 1);
    }

    @Test
    public void deserialiseEx2Step1() throws IOException {
        assertTransactionCount("/pacs.008.001.01_BusEx2_step1.xml", 1);
    }

    @Test
    public void deserialiseEx2Step2() throws IOException {
        final Pacs008 pacs008 = map("/pacs.008.001.01_BusEx2_step2.xml");

        assertEquals(1, pacs008.getCreditTransferList().getGroupHeader().getNumberOfTransactions());
        List<CreditTransfer> transfers = pacs008.getCreditTransferList().getCreditTransfers();
        assertEquals(1, transfers.size());
        assertThat(transfers.get(0).getCreditorAccount().getId().getIban(), is("IE29CCCC93115212345678"));
    }

    private void assertTransactionCount(String filename, int transactions) throws IOException {
        final Pacs008 pacs008 = map(filename);

        assertEquals(transactions, pacs008.getCreditTransferList().getGroupHeader().getNumberOfTransactions());
    	assertEquals(transactions, pacs008.getCreditTransferList().getCreditTransfers().size());
    }

    private Pacs008 map(String filename) throws IOException {
        return XML_MAPPER.readValue(Iso20022Test.class.getResourceAsStream(filename), Pacs008.class);
    }
}
