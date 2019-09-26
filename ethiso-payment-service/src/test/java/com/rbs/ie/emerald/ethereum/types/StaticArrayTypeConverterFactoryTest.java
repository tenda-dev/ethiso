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

public class StaticArrayTypeConverterFactoryTest {

	private static final TypeConverterManager TYPE_CONVERTER_MANAGER = new TypeConverterManager() {
		@Override
		public TypeConverter<?> getTypeConverter(final String type) {
			return new UIntTypeConverter(8);
		}
	};

	@Test
	public void uint8Array2() {
		assertNotNull(getTypeConverter("uint8[2]"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void negativeFixedArraySize() {
		getTypeConverter("uint8[-2]");
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidFixedArraySize() {
		getTypeConverter("uint8[oops]");
	}

	private TypeConverter<?> getTypeConverter(final String type) {
		return new StaticArrayTypeConverterFactory<>().newTypeConverter(TYPE_CONVERTER_MANAGER, type);
	}
}