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

import static com.rbs.ie.ethiso.ethereum.types.TypeConverterTestUtils.checkSerialisationBounds;
import static com.rbs.ie.ethiso.ethereum.types.TypeConverterTestUtils.deserialise;
import static com.rbs.ie.ethiso.ethereum.types.TypeConverterTestUtils.serialise;
import static com.rbs.ie.ethiso.ethereum.types.TypeConverterTestUtils.*;
import static java.math.BigInteger.ONE;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Test;

public class UIntTypeConverterTest {

	private static final BigInteger TWO = new BigInteger("2");
	private static final BigInteger MINUS_ONE = new BigInteger("-1");

	@Test
	public void serialiseUInt8() {
		assertArrayEquals(toArray("0000000000000000000000000000000000000000000000000000000000000010"),
				serialise(new UIntTypeConverter(8), new BigInteger("10", 16)));
	}

	@Test
	public void serialiseUInt8UpperBound() {
		checkSerialisationUpperBound(8);
	}

	@Test
	public void serialiseUInt8LowerBound() {
		checkSerialisationLowerBound(8);
	}

	@Test
	public void deserialiseUInt8() {
		assertEquals(new BigInteger("10", 16), deserialise(new UIntTypeConverter(8),
				toArray("0000000000000000000000000000000000000000000000000000000000000010")));
	}

	@Test
	public void deserialiseUInt8UpperBound() {
		checkDeserialisationUpperBound(8);
	}

	@Test
	public void serialiseUInt32() {
		assertArrayEquals(toArray("00000000000000000000000000000000000000000000000000000000ABCDEF01"),
				serialise(new UIntTypeConverter(32), new BigInteger("ABCDEF01", 16)));
	}

	@Test
	public void serialiseUInt32UpperBound() {
		checkSerialisationUpperBound(32);
	}

	@Test
	public void serialiseUInt32LowerBound() {
		checkSerialisationLowerBound(32);
	}

	@Test
	public void deserialiseUInt32() {
		assertEquals(new BigInteger("ABCDEF01", 16), deserialise(new UIntTypeConverter(32),
				toArray("00000000000000000000000000000000000000000000000000000000ABCDEF01")));
	}

	@Test
	public void deserialiseUInt32UpperBound() {
		checkDeserialisationUpperBound(32);
	}

	@Test
	public void serialiseUInt256() {
		assertArrayEquals(toArray("10000000000000000000000000000000000000000000000000000000ABCDEF01"),
				serialise(new UIntTypeConverter(256),
						new BigInteger("10000000000000000000000000000000000000000000000000000000ABCDEF01", 16)));
	}

	@Test
	public void serialiseUInt256UpperBound() {
		checkSerialisationUpperBound(256);
	}

	@Test
	public void serialiseUInt256LowerBound() {
		checkSerialisationLowerBound(256);
	}

	@Test
	public void deserialiseUInt256() {
		assertEquals(new BigInteger("10000000000000000000000000000000000000000000000000000000ABCDEF01", 16), deserialise(new UIntTypeConverter(256),
				toArray("10000000000000000000000000000000000000000000000000000000ABCDEF01")));
	}

	private void checkSerialisationUpperBound(final int size) {
		final BigInteger invalid = TWO.pow(size);
		final BigInteger valid = invalid.subtract(ONE);

		checkSerialisationBounds(new UIntTypeConverter(size), valid, invalid);
	}

	private void checkSerialisationLowerBound(final int size) {
		checkSerialisationBounds(new UIntTypeConverter(size), BigInteger.ZERO, MINUS_ONE);
	}

	private void checkDeserialisationUpperBound(final int size) {
		final byte[] valid = new byte[32];
		
		Arrays.fill(valid, 32 - (size / 8), 32, (byte) 0xFF);
		
		final byte[] invalid = new byte[32];
		
		invalid[32 - (size / 8) - 1] = 0x1;

		checkDeserialisationBounds(new UIntTypeConverter(size), valid, invalid);
	}
}