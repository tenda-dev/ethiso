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

import java.math.BigInteger;

import org.junit.Test;

public class IntTypeConverterFactoryTest {

	@Test
	public void intXXX() {
		for (int size = 8; size <= 256; size += 8) {
			assertNotNull(getTypeConverter("int" + size));
		}
	}

	@Test
	public void intSuccess() {
		assertNotNull(getTypeConverter("int"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void int10Fails() {
		getTypeConverter("int10");
	}

	@Test(expected = IllegalArgumentException.class)
	public void intPlus1Fails() {
		getTypeConverter("int+1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void intMinus1Fails() {
		getTypeConverter("int-1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void intDynamicArrayFails() {
		getTypeConverter("int[]");
	}

	@Test(expected = IllegalArgumentException.class)
	public void intStaticArrayFails() {
		getTypeConverter("int[2]");
	}

	private TypeConverter<BigInteger> getTypeConverter(final String type) {
		return new IntTypeConverterFactory().newTypeConverter(null, type);
	}
}