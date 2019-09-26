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

import org.junit.Test;

public class StaticBytesTypeConverterFactoryTest {

	@Test
	public void bytesXX() {
		for (int size = 2; size <= 32; size++) {
			assertNotNull(getTypeConverter("bytes" + size));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void bytes33Fails() {
		getTypeConverter("bytes33");
	}

	@Test(expected = IllegalArgumentException.class)
	public void bytesPlus1Fails() {
		getTypeConverter("bytes+1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void bytesMinus1Fails() {
		getTypeConverter("bytes-1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void bytesAFails() {
		getTypeConverter("bytesA");
	}

	@Test(expected = IllegalArgumentException.class)
	public void bytesDynamicArrayFails() {
		getTypeConverter("bytes[]");
	}

	@Test(expected = IllegalArgumentException.class)
	public void intStaticArrayFails() {
		getTypeConverter("bytes[2]");
	}

	private TypeConverter<byte[]> getTypeConverter(final String type) {
		return new StaticBytesTypeConverterFactory().newTypeConverter(null, type);
	}
}