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
package com.std.ie.ethiso.ethereum.types;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Test;

public class FixedTypeConverterFactoryTest {

	@Test
	public void fixedMxN() {
		for (int m = 8; m < 256; m += 8) {
			for (int n = 8; m + n <= 256; n += 8) {
				assertNotNull(getTypeConverter("fixed" + m + "x" + n));
			}
		}
	}

	@Test
	public void fixed() {
		assertNotNull(getTypeConverter("fixed"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void fixed10x8Fails() {
		getTypeConverter("fixed10x8");
	}

	@Test(expected = IllegalArgumentException.class)
	public void fixed8x10Fails() {
		getTypeConverter("fixed8x10");
	}

	@Test(expected = IllegalArgumentException.class)
	public void fixedPlus8x8Fails() {
		getTypeConverter("fixed+8x8");
	}

	@Test(expected = IllegalArgumentException.class)
	public void fixedMinus1Fails() {
		getTypeConverter("fixed-1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void fixed8xAFails() {
		getTypeConverter("fixed8xA");
	}

	@Test(expected = IllegalArgumentException.class)
	public void fixedAx8Fails() {
		getTypeConverter("fixedAx8");
	}

	@Test(expected = IllegalArgumentException.class)
	public void fixed8Fails() {
		getTypeConverter("fixed8");
	}

	@Test(expected = IllegalArgumentException.class)
	public void fixedDynamicArrayFails() {
		getTypeConverter("fixed[]");
	}

	@Test(expected = IllegalArgumentException.class)
	public void fixedStaticArrayFails() {
		getTypeConverter("fixed[2]");
	}

	private TypeConverter<BigDecimal> getTypeConverter(final String type) {
		return new FixedTypeConverterFactory().newTypeConverter(null, type);
	}
}