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
package com.iso.ie.ethiso;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.Future;

import com.iso.ie.ethiso.domain.Bic;
import com.iso.ie.ethiso.domain.Currency;
import com.iso.ie.ethiso.domain.Iban;
import com.iso.ie.ethiso.domain.Party;

public interface PaymentService {

    boolean registerBic(Bic bic);

    void setAllow(Party them, Currency currency, BigInteger allowedAmount);

    Future<Void> setAllowAsync(Party them, Currency currency, BigInteger allowedAmount);

    void setLimit(Party them, Currency currency, BigInteger limitAmount);

    Future<Void> setLimitAsync(Party them, Currency currency, BigInteger allowedAmount);

    void sendPayment(Bic fromBic, Bic toBic, Iban from, Iban to, Currency currency, BigInteger paymentAmount, Map<String, Object> additionalProperties);

    Future<Void> sendPaymentAsync(Bic fromBic, Bic toBic, Iban from, Iban to, Currency currency, BigInteger paymentAmount, Map<String, Object> additionalProperties);

    void registerPaymentListener(PaymentListener paymentListener);

    void registerTrustlineListener(TrustlineListener trustlineListener);

    BigInteger getBalance(Party them, Currency currency);
}