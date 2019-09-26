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
package com.std.ie.ethiso.domain;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static com.std.ie.ethiso.domain.Currency.EUR;
import static org.junit.Assert.assertThat;

public class TrustlineKeyTest {

    @Test
    public void partyOrderShouldNotMatter() {
        Party p1 = Party.party("BNP");
        Party p2 = Party.party("UB");

        TrustlineKey k1 = new TrustlineKey(p1, p2, EUR);
        TrustlineKey k2 = new TrustlineKey(p2, p1, EUR);

        assertThat(k1, CoreMatchers.equalTo(k2));
    }
}
