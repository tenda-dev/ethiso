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
package com.iso.ie.ethiso.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class IbanTest {

    private final Iban iban = Iban.iban("someIban");
    private final Iban identicalIban = Iban.iban("someIban");
    private final Iban differentIban = Iban.iban("someOtherIban");

    @Test
    public void ibanShouldBeEqualButNotSame() {
        assertThat(iban, equalTo(identicalIban));
        assertThat(iban, not(sameInstance(identicalIban)));
    }

    @Test
    public void differentIbansShouldNotBeEqual() {
        assertThat(iban, not(equalTo(differentIban)));
    }
}