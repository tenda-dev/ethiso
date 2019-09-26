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
package com.rbs.ie.ethiso.iso.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PaymentReturnList implements Iterable<PaymentReturn> {

    private GroupHeader groupHeader;

    @JacksonXmlElementWrapper(useWrapping = false)

    @JsonProperty("GrpHdr")
    public GroupHeader getGroupHeader() {
        return groupHeader;
    }

    public void setGroupHeader(GroupHeader groupHeader) {
        this.groupHeader = groupHeader;
    }

    @JacksonXmlProperty(localName = "TxInf")
    private List<PaymentReturn> paymentReturns = new ArrayList<>();

    public List<PaymentReturn> getPaymentReturns() {
        return paymentReturns;
    }

    public void setPaymentReturns(PaymentReturn paymentReturn) {
        paymentReturns.add(paymentReturn);
    }

    @Override
    public Iterator<PaymentReturn> iterator() {
        return paymentReturns.iterator();
    }
}
