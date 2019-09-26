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
package com.rbs.ie.ethiso.domain;

import static java.math.BigInteger.TEN;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TrustlineValueTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeAbleToSendMoneyIfItExceedsAllow() {
        TrustlineValue trustlineValue = new TrustlineValue();

        trustlineValue.reverse().setAllow(TEN);

        trustlineValue.sendPayment(TEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeAbleToSendMoneyIfItExceedsLimit() {
        TrustlineValue trustlineValue = new TrustlineValue();

        trustlineValue.setLimit(TEN);
        trustlineValue.sendPayment(TEN);
    }

    @Test
    public void shouldBeAbleToSendMoneyIfItIsUnderLimitAndAllow() {
        TrustlineValue trustlineValue = new TrustlineValue();

        trustlineValue.reverse().setAllow(TEN);
        trustlineValue.setLimit(TEN);
        trustlineValue.sendPayment(TEN);
    }

    @Test
    public void moneyOwedShouldBeEqualAndOpposite() {
        TrustlineValue trustlineValue = new TrustlineValue();

        trustlineValue.view.setP2OwesP1(TEN);

        assertEquals(TEN.negate(), trustlineValue.reverse().view.getP2OwesP1());
    }
}
