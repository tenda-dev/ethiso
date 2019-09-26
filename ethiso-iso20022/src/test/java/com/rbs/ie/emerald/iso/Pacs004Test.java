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

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.rbs.ie.emerald.iso.domain.PaymentReturn;
import com.rbs.ie.emerald.iso.domain.Pacs004;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class Pacs004Test {
    private static final XmlMapper XML_MAPPER = new XmlMapper();


    @Test
    public void checkTransactionCounts() throws IOException {
        final Pacs004 pacs004 = map("/pacs.004.test1.xml");

        assertEquals(3, pacs004.getPaymentReturnList().getGroupHeader().getNumberOfTransactions());
        List<PaymentReturn> returns = pacs004.getPaymentReturnList().getPaymentReturns();
        assertEquals(3, returns.size());
    }


    @Test
    public void checkSomeDetails() throws IOException {
        final Pacs004 pacs004 = map("/pacs.004.test1.xml");

        assertEquals(3, pacs004.getPaymentReturnList().getGroupHeader().getNumberOfTransactions());
        assertThat( pacs004.getPaymentReturnList().getGroupHeader().getTotalReturnedInterbankSettlementAmount().getAmount(), is(new BigDecimal("60.06")));

        List<PaymentReturn> returns = pacs004.getPaymentReturnList().getPaymentReturns();
        assertThat(returns.get(0).getOriginalEndToEndIdentification(), is("endtoend"));
        assertThat(returns.get(0).getOriginalTransactionIdentification(), is("GTSBS1O160330001"));
        assertThat(returns.get(0).getOriginalTransactionReference().getSettlementInformation().getSettlementMethod(), is("CLRG"));
        assertThat(returns.get(0).getReturnReasonInformation().getAdditionalInformation(), is("AIB"));
        assertThat(returns.get(2).getOriginalTransactionIdentification(), is("GTSBS2O160330003"));

    }


    private Pacs004 map(String filename) throws IOException {
        return XML_MAPPER.readValue(Pacs004Test.class.getResourceAsStream(filename), Pacs004.class);
    }
}
