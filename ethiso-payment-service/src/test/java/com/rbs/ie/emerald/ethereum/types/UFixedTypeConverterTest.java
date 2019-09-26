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

import static com.rbs.ie.emerald.ethereum.types.TypeConverterTestUtils.checkDeserialisationBounds;
import static com.rbs.ie.emerald.ethereum.types.TypeConverterTestUtils.checkSerialisationBounds;
import static com.rbs.ie.emerald.ethereum.types.TypeConverterTestUtils.deserialise;
import static com.rbs.ie.emerald.ethereum.types.TypeConverterTestUtils.serialise;
import static com.rbs.ie.emerald.ethereum.types.TypeConverterTestUtils.toArray;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class UFixedTypeConverterTest {

	private static final BigDecimal TWO = new BigDecimal("2");

	@Test
	public void serialiseUFixed8x8() {
		assertArrayEquals(toArray("0000000000000000000000000000000000000000000000000000000000000180"),
				serialise(new UFixedTypeConverter(8, 8), new BigDecimal("1.5")));
	}

	@Test
	public void serialiseUFixed8x8UpperBound() {
		checkSerialisationUpperBound(8, 8);
	}

	@Test
	public void serialiseUFixed8x8FractionalBound() {
		checkSerialisationFractionalBound(8, 8);
	}

	@Test
	public void serialiseUFixed8x8LowerBound() {
		checkSerialisationLowerBound(8, 8);
	}

	@Test
	public void deserialiseUFixed8x8() {
		assertEquals(new BigDecimal("1.5"), deserialise(new UFixedTypeConverter(8, 8),
				toArray("0000000000000000000000000000000000000000000000000000000000000180")));
	}

	@Test
	public void deserialiseUFixed8x8UpperBound() {
		checkDeserialisationUpperBound(8, 8);
	}

	@Test
	public void serialiseUFixed24x8() {
		assertArrayEquals(toArray("0000000000000000000000000000000000000000000000000000000001000040"),
				serialise(new UFixedTypeConverter(24, 8), new BigDecimal("65536.25")));
	}

	@Test
	public void serialiseUFixed24x8UpperBound() {
		checkSerialisationUpperBound(24, 8);
	}

	@Test
	public void serialiseUFixed24x8FractionalBound() {
		checkSerialisationFractionalBound(24, 8);
	}

	@Test
	public void serialiseUFixed24x8LowerBound() {
		checkSerialisationLowerBound(24, 8);
	}

	@Test
	public void deserialiseUFixed24x8() {
		assertEquals(new BigDecimal("65536.25"), deserialise(new UFixedTypeConverter(24, 8),
				toArray("0000000000000000000000000000000000000000000000000000000001000040")));
	}

	@Test
	public void deserialiseUFixed24x8UpperBound() {
		checkDeserialisationUpperBound(24, 8);
	}

	@Test
	public void serialiseUFixed128x128() {
		assertArrayEquals(toArray("0000000000000000000000000001000080000000000000000000000000000000"),
				serialise(new UFixedTypeConverter(128, 128), new BigDecimal("65536.5")));
	}

	@Test
	public void serialiseUFixed128x128UpperBound() {
		checkSerialisationUpperBound(128, 128);
	}

	@Test
	public void serialiseUFixed128x128FractionalBound() {
		checkSerialisationFractionalBound(128, 128);
	}

	@Test
	public void serialiseUFixed128x128LowerBound() {
		checkSerialisationLowerBound(128, 128);
	}

	@Test
	public void deserialiseUFixed128x128() {
		assertEquals(new BigDecimal("65536.5"), deserialise(new UFixedTypeConverter(128, 128),
				toArray("0000000000000000000000000001000080000000000000000000000000000000")));
	}

	private void checkSerialisationUpperBound(final int m, final int n) {
		final BigDecimal invalid = TWO.pow(m);
		final BigDecimal valid = invalid.subtract(ONE);

		checkSerialisationBounds(new UFixedTypeConverter(m, n), valid, invalid);
	}

	private void checkSerialisationFractionalBound(final int m, final int n) {
		final BigDecimal fractionalInvalid = ONE.divide(TWO.pow(n + 1));
		final BigDecimal fractionalValid = ONE.divide(TWO.pow(n));

		checkSerialisationBounds(new UFixedTypeConverter(m, n), fractionalValid, fractionalInvalid);
	}

	private void checkSerialisationLowerBound(final int m, final int n) {
		final BigDecimal valid = ZERO;
		final BigDecimal invalid = valid.subtract(ONE);

		checkSerialisationBounds(new UFixedTypeConverter(m, n), valid, invalid);
	}

	private void checkDeserialisationUpperBound(final int m, final int n) {
		final byte[] valid = new byte[32];

		valid[32 - (m + n) / 8] = (byte) 0xEF;

		Arrays.fill(valid, 32 - (m + n) / 8, 32, (byte) 0xFF);

		final byte[] invalid = new byte[32];

		Arrays.fill(invalid, 32 - (m + n) / 8 - 1, 32, (byte) 0xFF);

		checkDeserialisationBounds(new UFixedTypeConverter(m, n), valid, invalid);
	}
}