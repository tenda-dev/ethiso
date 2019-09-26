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

public class IntTypeConverterTest {

	private static final BigInteger TWO = new BigInteger("2");

	@Test
	public void serialiseInt8() {
		assertArrayEquals(toArray("0000000000000000000000000000000000000000000000000000000000000010"),
				serialise(new IntTypeConverter(8), new BigInteger("10", 16)));
	}

	@Test
	public void serialiseInt8Negative() {
		assertArrayEquals(toArray("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0"),
				serialise(new IntTypeConverter(8), new BigInteger("-10", 16)));
	}

	@Test
	public void serialiseInt8UpperBound() {
		checkSerialisationUpperBound(8);
	}

	@Test
	public void serialiseInt8LowerBound() {
		checkSerialisationLowerBound(8);
	}

	@Test
	public void deserialiseInt8() {
		assertEquals(new BigInteger("10", 16), deserialise(new IntTypeConverter(8),
				toArray("0000000000000000000000000000000000000000000000000000000000000010")));
	}

	@Test
	public void deserialiseInt8Negative() {
		assertEquals(new BigInteger("-10", 16), deserialise(new IntTypeConverter(8),
				toArray("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0")));
	}

	@Test
	public void deserialiseInt8UpperBound() {
		checkDeserialisationUpperBound(8);
	}

	@Test
	public void deserialiseInt8LowerBound() {
		checkDeserialisationLowerBound(8);
	}

	@Test
	public void serialiseInt32() {
		assertArrayEquals(toArray("000000000000000000000000000000000000000000000000000000007BCDEF01"),
				serialise(new IntTypeConverter(32), new BigInteger("7BCDEF01", 16)));
	}

	@Test
	public void serialiseInt32Negative() {
		assertArrayEquals(toArray("ffffffffffffffffffffffffffffffffffffffffffffffffffffffff843210ff"),
				serialise(new IntTypeConverter(32), new BigInteger("-7BCDEF01", 16)));
	}

	@Test
	public void serialiseInt32UpperBound() {
		checkSerialisationUpperBound(32);
	}

	@Test
	public void serialiseInt32LowerBound() {
		checkSerialisationLowerBound(32);
	}

	@Test
	public void deserialiseInt32() {
		assertEquals(new BigInteger("7BCDEF01", 16), deserialise(new IntTypeConverter(32),
				toArray("000000000000000000000000000000000000000000000000000000007BCDEF01")));
	}

	@Test
	public void deserialiseInt32Negative() {
		assertEquals(new BigInteger("-7BCDEF01", 16), deserialise(new IntTypeConverter(32),
				toArray("ffffffffffffffffffffffffffffffffffffffffffffffffffffffff843210ff")));
	}

	@Test
	public void deserialiseInt32UpperBound() {
		checkDeserialisationUpperBound(32);
	}

	@Test
	public void deserialiseInt32LowerBound() {
		checkDeserialisationLowerBound(32);
	}

	@Test
	public void serialiseInt256() {
		assertArrayEquals(toArray("10000000000000000000000000000000000000000000000000000000ABCDEF01"),
				serialise(new IntTypeConverter(256),
						new BigInteger("10000000000000000000000000000000000000000000000000000000ABCDEF01", 16)));
	}

	@Test
	public void serialiseInt256Negative() {
		assertArrayEquals(toArray("efffffffffffffffffffffffffffffffffffffffffffffffffffffff543210ff"),
				serialise(new IntTypeConverter(256),
						new BigInteger("-10000000000000000000000000000000000000000000000000000000ABCDEF01", 16)));
	}

	@Test
	public void serialiseInt256UpperBound() {
		checkSerialisationUpperBound(256);
	}

	@Test
	public void serialiseInt256LowerBound() {
		checkSerialisationLowerBound(256);
	}

	@Test
	public void deserialiseInt256() {
		assertEquals(new BigInteger("10000000000000000000000000000000000000000000000000000000ABCDEF01", 16),
				deserialise(new IntTypeConverter(256),
						toArray("10000000000000000000000000000000000000000000000000000000ABCDEF01")));
	}

	@Test
	public void deserialiseInt256Negative() {
		assertEquals(new BigInteger("-10000000000000000000000000000000000000000000000000000000ABCDEF01", 16),
				deserialise(new IntTypeConverter(256),
						toArray("efffffffffffffffffffffffffffffffffffffffffffffffffffffff543210ff")));
	}

	private void checkSerialisationUpperBound(final int size) {
		final BigInteger invalid = TWO.pow(size - 1);
		final BigInteger valid = invalid.subtract(ONE);

		checkSerialisationBounds(new IntTypeConverter(size), valid, invalid);
	}

	private void checkSerialisationLowerBound(final int size) {
		final BigInteger valid = TWO.pow(size - 1).negate();
		final BigInteger invalid = valid.subtract(ONE);

		checkSerialisationBounds(new IntTypeConverter(size), valid, invalid);
	}

	private void checkDeserialisationUpperBound(final int size) {
		final byte[] valid = new byte[32];

		valid[32 - (size / 8)] = (byte) 0xEF;

		Arrays.fill(valid, 32 - (size / 8), 32, (byte) 0xFF);

		final byte[] invalid = new byte[32];

		Arrays.fill(invalid, 32 - (size / 8) - 1, 32, (byte) 0xFF);

		checkDeserialisationBounds(new IntTypeConverter(size), valid, invalid);
	}

	private void checkDeserialisationLowerBound(final int size) {
		final byte[] valid = new byte[32];

		Arrays.fill(valid, 0, 32 - (size / 8), (byte) 0xFF);
		valid[32 - (size / 8)] = (byte) 0x80;

		final byte[] invalid = new byte[32];

		Arrays.fill(invalid, 0, 32 - (size / 8), (byte) 0xFF);

		checkDeserialisationBounds(new IntTypeConverter(size), valid, invalid);
	}
}