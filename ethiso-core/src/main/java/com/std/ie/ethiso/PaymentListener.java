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
package com.std.ie.ethiso;

import java.math.BigInteger;
import java.util.Map;

import com.std.ie.ethiso.domain.Bic;
import com.std.ie.ethiso.domain.Currency;
import com.std.ie.ethiso.domain.Iban;

public interface PaymentListener {
    void paymentReceived(Bic fromBic, Bic toBic, Iban fromIban, Iban toIban, Currency currency, BigInteger paymentAmount, Map<String, Object> additionalProperties);
}
