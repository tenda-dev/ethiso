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

import static com.std.ie.ethiso.ethereum.types.TypeConverterTestUtils.checkDeserialisationBounds;
import static com.std.ie.ethiso.ethereum.types.TypeConverterTestUtils.checkSerialisationBounds;
import static com.std.ie.ethiso.ethereum.types.TypeConverterTestUtils.deserialise;
import static com.std.ie.ethiso.ethereum.types.TypeConverterTestUtils.serialise;
import static com.std.ie.ethiso.ethereum.types.TypeConverterTestUtils.toArray;
import static java.math.BigDecimal.ONE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class FixedTypeConverterTest {

	private static final BigDecimal TWO = new BigDecimal("2");

	@Test
	public void serialiseFixed8x8() {
		assertArrayEquals(toArray("0000000000000000000000000000000000000000000000000000000000000180"),
				serialise(new FixedTypeConverter(8, 8), new BigDecimal("1.5")));
	}

	@Test
	public void serialiseFixed8x8Negative() {
		assertArrayEquals(toArray("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe80"),
				serialise(new FixedTypeConverter(8, 8), new BigDecimal("-1.5")));
	}

	@Test
	public void serialiseFixed8x8UpperBound() {
		checkSerialisationUpperBound(8, 8);
	}

	@Test
	public void serialiseFixed8x8FractionalBound() {
		checkSerialisationFractionalBound(8, 8);
	}

	@Test
	public void serialiseFixed8x8LowerBound() {
		checkSerialisationLowerBound(8, 8);
	}

	@Test
	public void deserialiseFixed8x8() {
		assertEquals(new BigDecimal("1.5"), deserialise(new FixedTypeConverter(8, 8),
				toArray("0000000000000000000000000000000000000000000000000000000000000180")));
	}

	@Test
	public void deserialiseFixed8x8Negative() {
		assertEquals(new BigDecimal("-1.5"), deserialise(new FixedTypeConverter(8, 8),
				toArray("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe80")));
	}

	@Test
	public void deserialiseFixed8x8UpperBound() {
		checkDeserialisationUpperBound(8, 8);
	}

	@Test
	public void deserialiseFixed8x8LowerBound() {
		checkDeserialisationLowerBound(8, 8);
	}

	@Test
	public void serialiseFixed24x8() {
		assertArrayEquals(toArray("0000000000000000000000000000000000000000000000000000000001000040"),
				serialise(new FixedTypeConverter(24, 8), new BigDecimal("65536.25")));
	}

	@Test
	public void serialiseFixed24x8Negative() {
		assertArrayEquals(toArray("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffeffffc0"),
				serialise(new FixedTypeConverter(24, 8), new BigDecimal("-65536.25")));
	}

	@Test
	public void serialiseFixed24x8UpperBound() {
		checkSerialisationUpperBound(24, 8);
	}

	@Test
	public void serialiseFixed24x8FractionalBound() {
		checkSerialisationFractionalBound(24, 8);
	}

	@Test
	public void serialiseFixed24x8LowerBound() {
		checkSerialisationLowerBound(24, 8);
	}

	@Test
	public void deserialiseFixed24x8() {
		assertEquals(new BigDecimal("65536.25"), deserialise(new FixedTypeConverter(24, 8),
				toArray("0000000000000000000000000000000000000000000000000000000001000040")));
	}

	@Test
	public void deserialiseFixed24x8Negative() {
		assertEquals(new BigDecimal("-65536.25"), deserialise(new FixedTypeConverter(24, 8),
				toArray("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffeffffc0")));
	}

	@Test
	public void deserialiseFixed24x8UpperBound() {
		checkDeserialisationUpperBound(24, 8);
	}

	@Test
	public void deserialiseFixed24x8LowerBound() {
		checkDeserialisationLowerBound(24, 8);
	}

	@Test
	public void serialiseFixed128x128() {
		assertArrayEquals(toArray("0000000000000000000000000001000080000000000000000000000000000000"),
				serialise(new FixedTypeConverter(128, 128), new BigDecimal("65536.5")));
	}

	@Test
	public void serialiseFixed128x128Negative() {
		assertArrayEquals(toArray("fffffffffffffffffffffffffffeffff80000000000000000000000000000000"),
				serialise(new FixedTypeConverter(128, 128), new BigDecimal("-65536.5")));
	}

	@Test
	public void serialiseFixed128x128UpperBound() {
		checkSerialisationUpperBound(128, 128);
	}

	@Test
	public void serialiseFixed128x128FractionalBound() {
		checkSerialisationFractionalBound(128, 128);
	}

	@Test
	public void serialiseFixed128x128LowerBound() {
		checkSerialisationLowerBound(128, 128);
	}

	@Test
	public void deserialiseFixed128x128() {
		assertEquals(new BigDecimal("65536.5"), deserialise(new FixedTypeConverter(128, 128),
				toArray("0000000000000000000000000001000080000000000000000000000000000000")));
	}

	@Test
	public void deserialiseFixed128x128Negative() {
		assertEquals(new BigDecimal("-65536.5"), deserialise(new FixedTypeConverter(128, 128),
				toArray("fffffffffffffffffffffffffffeffff80000000000000000000000000000000")));
	}

	private void checkSerialisationUpperBound(final int m, final int n) {
		final BigDecimal invalid = TWO.pow(m - 1);
		final BigDecimal valid = invalid.subtract(ONE);

		checkSerialisationBounds(new FixedTypeConverter(m, n), valid, invalid);
	}

	private void checkSerialisationFractionalBound(final int m, final int n) {
		final BigDecimal fractionalInvalid = ONE.divide(TWO.pow(n + 1));
		final BigDecimal fractionalValid = ONE.divide(TWO.pow(n));

		checkSerialisationBounds(new FixedTypeConverter(m, n), fractionalValid, fractionalInvalid);
	}

	private void checkSerialisationLowerBound(final int m, final int n) {
		final BigDecimal valid = TWO.pow(m - 1).negate();
		final BigDecimal invalid = valid.subtract(ONE);

		checkSerialisationBounds(new FixedTypeConverter(m, n), valid, invalid);
	}

	private void checkDeserialisationUpperBound(final int m, final int n) {
		final byte[] valid = new byte[32];

		valid[32 - (m + n) / 8] = (byte) 0xEF;

		Arrays.fill(valid, 32 - (m + n) / 8, 32, (byte) 0xFF);

		final byte[] invalid = new byte[32];

		Arrays.fill(invalid, 32 - (m + n) / 8 - 1, 32, (byte) 0xFF);

		checkDeserialisationBounds(new FixedTypeConverter(m, n), valid, invalid);
	}

	private void checkDeserialisationLowerBound(final int m, final int n) {
		final byte[] valid = new byte[32];

		Arrays.fill(valid, 0, 32 - (m + n) / 8, (byte) 0xFF);
		valid[32 - (m + n) / 8] = (byte) 0x80;

		final byte[] invalid = new byte[32];

		Arrays.fill(invalid, 0, 32 - (m + n) / 8, (byte) 0xFF);

		checkDeserialisationBounds(new FixedTypeConverter(m, n), valid, invalid);
	}
}