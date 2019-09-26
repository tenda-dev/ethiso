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
package com.iso.ie.ethiso.ethereum.types;

import static com.iso.ie.ethiso.ethereum.types.TypeConverterTestUtils.deserialise;
import static com.iso.ie.ethiso.ethereum.types.TypeConverterTestUtils.serialise;
import static com.iso.ie.ethiso.ethereum.types.TypeConverterTestUtils.toArray;
import static org.junit.Assert.assertArrayEquals;

import java.math.BigInteger;

import org.junit.Test;

public class DynamicArrayTypeConverterTest {

	@Test
	public void serialiseUInt8Array2() {
		assertArrayEquals(
				toArray("000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000010"),
				serialise(new DynamicArrayTypeConverter<>(new UIntTypeConverter(8), BigInteger.class),
						new BigInteger[] { new BigInteger("20", 16), new BigInteger("10", 16) }));
	}

	@Test
	public void deserialiseUInt8Array2() {
		assertArrayEquals(new BigInteger[] { new BigInteger("20", 16), new BigInteger("10", 16) }, deserialise(
				new DynamicArrayTypeConverter<>(new UIntTypeConverter(8), BigInteger.class),
				toArray("000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000010")));
	}
}