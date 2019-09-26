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
package com.iso.ie.ethiso.iso.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupHeader {
    private String messageId;
    private Date creditDateTime;
    private int numberOfTransactions;
    private Amount totalInterbankSettlementAmount;
    private Date interbankSettlementDate;
    private SettlementInfo settlementInfo;
    private Agent instructingAgent;
    private Agent instructedAgent;
    private Amount totalReturnedInterbankSettlementAmount;

    @JsonProperty("MsgId")
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("CreDtTm")
    public Date getCreditDateTime() {
        return creditDateTime;
    }

    public void setCreditDateTime(Date creditDateTime) {
        this.creditDateTime = creditDateTime;
    }

    @JsonProperty("NbOfTxs")
    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(int numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }

    @JsonProperty("TtlIntrBkSttlmAmt")
    public Amount getTotalInterbankSettlementAmount() {
        return totalInterbankSettlementAmount;
    }

    public void setTotalInterbankSettlementAmount(Amount totalInterbankSettlementAmount) {
        this.totalInterbankSettlementAmount = totalInterbankSettlementAmount;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("IntrBkSttlmDt")
    public Date getInterbankSettlementDate() {
        return interbankSettlementDate;
    }

    public void setInterbankSettlementDate(Date interbankSettlementDate) {
        this.interbankSettlementDate = interbankSettlementDate;
    }

    @JsonProperty("SttlmInf")
    public SettlementInfo getSettlementInfo() {
        return settlementInfo;
    }

    public void setSettlementInfo(SettlementInfo settlementInfo) {
        this.settlementInfo = settlementInfo;
    }

    @JsonProperty("InstgAgt")
    public Agent getInstructingAgent() {
        return instructingAgent;
    }

    public void setInstructingAgent(Agent instructingAgent) {
        this.instructingAgent = instructingAgent;
    }

    @JsonProperty("InstdAgt")
    public Agent getInstructedAgent() {
        return instructedAgent;
    }

    public void setInstructedAgent(Agent instructedAgent) {
        this.instructedAgent = instructedAgent;
    }

    @JsonProperty("TtlRtrdIntrBkSttlmAmt")
    public Amount getTotalReturnedInterbankSettlementAmount() {
        return totalReturnedInterbankSettlementAmount;
    }

    public void setTotalReturnedInterbankSettlementAmount(Amount totalReturnedInterbankSettlementAmount) {
        this.totalReturnedInterbankSettlementAmount = totalReturnedInterbankSettlementAmount;
    }
}