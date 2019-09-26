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
package com.rbs.ie.emerald.ethereum.types;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Test;

public class UFixedTypeConverterFactoryTest {

	@Test
	public void ufixedMxN() {
		for (int m = 8; m < 256; m += 8) {
			for (int n = 8; m + n <= 256; n += 8) {
				assertNotNull(getTypeConverter("ufixed" + m + "x" + n));
			}
		}
	}

	@Test
	public void ufixed() {
		assertNotNull(getTypeConverter("ufixed"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void ufixed10x8Fails() {
		getTypeConverter("ufixed10x8");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ufixed8x10Fails() {
		getTypeConverter("ufixed8x10");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ufixedPlus8x8Fails() {
		getTypeConverter("ufixed+8x8");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ufixedMinus1Fails() {
		getTypeConverter("ufixed-1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ufixed8xAFails() {
		getTypeConverter("ufixed8xA");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ufixedAx8Fails() {
		getTypeConverter("ufixedAx8");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ufixed8Fails() {
		getTypeConverter("ufixed8");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ufixedDynamicArrayFails() {
		getTypeConverter("ufixed[]");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ufixedStaticArrayFails() {
		getTypeConverter("ufixed[2]");
	}

	private TypeConverter<BigDecimal> getTypeConverter(final String type) {
		return new UFixedTypeConverterFactory().newTypeConverter(null, type);
	}
}