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

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentReturn {

    private String returnIdentification;
    private OriginalGroupInformation originalGroupInformation;
    private String originalEndToEndIdentification;
    private String originalTransactionIdentification;
    private Amount originalInterbankSettlementAmount;
    private Amount returnedInterbankSettlementAmount;
    private Amount returnedInstructedAmount;
    private String chargeBearer;
    private Agent instructingAgent;
    private StatusReasonInfo returnReasonInformation;
    private OriginalTransactionReference originalTransactionReference;

    @JsonProperty("RtrId")
    public String getReturnIdentification() {
        return returnIdentification;
    }

    public void setReturnIdentification(String returnIdentification) {
        this.returnIdentification = returnIdentification;
    }

    @JsonProperty("OrgnlGrpInf")
    public OriginalGroupInformation getOriginalGroupInformation() {
        return originalGroupInformation;
    }

    public void setOriginalGroupInformation(OriginalGroupInformation originalGroupInformation) {
        this.originalGroupInformation = originalGroupInformation;
    }

    @JsonProperty("OrgnlEndToEndId")
    public String getOriginalEndToEndIdentification() {
        return originalEndToEndIdentification;
    }

    public void setOriginalEndToEndIdentification(String originalEndToEndIdentification) {
        this.originalEndToEndIdentification = originalEndToEndIdentification;
    }

    @JsonProperty("OrgnlTxId")
    public String getOriginalTransactionIdentification() {
        return originalTransactionIdentification;
    }

    public void setOriginalTransactionIdentification(String originalTransactionIdentification) {
        this.originalTransactionIdentification = originalTransactionIdentification;
    }

    @JsonProperty("OrgnlIntrBkSttlmAmt")
    public Amount getOriginalInterbankSettlementAmount() {
        return originalInterbankSettlementAmount;
    }

    public void setOriginalInterbankSettlementAmount(Amount originalInterbankSettlementAmount) {
        this.originalInterbankSettlementAmount = originalInterbankSettlementAmount;
    }

    @JsonProperty("RtrdIntrBkSttlmAmt")
    public Amount getReturnedInterbankSettlementAmount() {
        return returnedInterbankSettlementAmount;
    }

    public void setReturnedInterbankSettlementAmount(Amount returnedInterbankSettlementAmount) {
        this.returnedInterbankSettlementAmount = returnedInterbankSettlementAmount;
    }

    @JsonProperty("RtrdInstdAmt")
    public Amount getReturnedInstructedAmount() {
        return returnedInstructedAmount;
    }

    public void setReturnedInstructedAmount(Amount returnedInstructedAmount) {
        this.returnedInstructedAmount = returnedInstructedAmount;
    }

    @JsonProperty("ChrgBr")
    public String getChargeBearer() {
        return chargeBearer;
    }

    public void setChargeBearer(String chargeBearer) {
        this.chargeBearer = chargeBearer;
    }

    @JsonProperty("InstgAgt")
    public Agent getInstructingAgent() {
        return instructingAgent;
    }

    public void setInstructingAgent(Agent instructingAgent) {
        this.instructingAgent = instructingAgent;
    }

    @JsonProperty("RtrRsnInf")
    public StatusReasonInfo getReturnReasonInformation() {
        return returnReasonInformation;
    }

    public void setReturnReasonInformation(StatusReasonInfo returnReasonInformation) {
        this.returnReasonInformation = returnReasonInformation;
    }

    @JsonProperty("OrgnlTxRef")
    public OriginalTransactionReference getOriginalTransactionReference() {
        return originalTransactionReference;
    }

    public void setOriginalTransactionReference(OriginalTransactionReference originalTransactionReference) {
        this.originalTransactionReference = originalTransactionReference;
    }
}


