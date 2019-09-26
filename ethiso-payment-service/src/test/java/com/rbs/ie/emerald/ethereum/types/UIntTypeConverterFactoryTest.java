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
package com.rbs.ie.ethiso.ethereum.types;

import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;

import org.junit.Test;

public class UIntTypeConverterFactoryTest {

	@Test
	public void uintXXX() {
		for (int size = 8; size <= 256; size += 8) {
			assertNotNull(getTypeConverter("uint" + size));
		}
	}

	@Test
	public void uint() {
		assertNotNull(getTypeConverter("uint"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void uint10Fails() {
		getTypeConverter("uint10");
	}

	@Test(expected = IllegalArgumentException.class)
	public void uintPlus1Fails() {
		getTypeConverter("uint+1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void uintMinus1Fails() {
		getTypeConverter("uint-1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void uintDynamicArrayFails() {
		getTypeConverter("uint[]");
	}

	@Test(expected = IllegalArgumentException.class)
	public void uintStaticArrayFails() {
		getTypeConverter("uint[2]");
	}

	private TypeConverter<BigInteger> getTypeConverter(final String type) {
		return new UIntTypeConverterFactory().newTypeConverter(null, type);
	}
}