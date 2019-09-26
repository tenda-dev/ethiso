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

import static com.std.ie.ethiso.ethereum.types.TypeConverterTestUtils.deserialise;
import static com.std.ie.ethiso.ethereum.types.TypeConverterTestUtils.serialise;
import static com.std.ie.ethiso.ethereum.types.TypeConverterTestUtils.toArray;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class StaticBytesTypeConverterTest {

	@Test
	public void serialiseBytes1() {
		assertArrayEquals(toArray("1000000000000000000000000000000000000000000000000000000000000000"),
				serialise(new StaticBytesTypeConverter(1), new byte[] { 0x10 }));
	}

	@Test(expected = IllegalArgumentException.class)
	public void serialiseBytes1ArrayTooShort() {
		serialise(new StaticBytesTypeConverter(1), new byte[] {});
	}

	@Test(expected = IllegalArgumentException.class)
	public void serialiseBytes1ArrayTooLong() {
		serialise(new StaticBytesTypeConverter(1), new byte[] { 0x10, 0x00 });
	}

	@Test
	public void deserialiseBytes1() {
		assertArrayEquals(new byte[] { 0x10 }, deserialise(new StaticBytesTypeConverter(1),
				toArray("1000000000000000000000000000000000000000000000000000000000000000")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void deserialiseBytes1IllegalPadding() {
		deserialise(new StaticBytesTypeConverter(1),
				toArray("1010000000000000000000000000000000000000000000000000000000000000"));
	}

	@Test
	public void serialiseBytes4() {
		assertArrayEquals(toArray("1011121300000000000000000000000000000000000000000000000000000000"),
				serialise(new StaticBytesTypeConverter(4), new byte[] { 0x10, 0x11, 0x12, 0x13 }));
	}

	@Test(expected = IllegalArgumentException.class)
	public void serialiseBytes4ArrayTooShort() {
		serialise(new StaticBytesTypeConverter(4), new byte[] {});
	}

	@Test(expected = IllegalArgumentException.class)
	public void serialiseBytes4ArrayTooLong() {
		serialise(new StaticBytesTypeConverter(4), new byte[] { 0x10, 0x11, 0x12, 0x13, 0x00 });
	}

	@Test
	public void deserialiseBytes4() {
		assertArrayEquals(new byte[] { 0x10, 0x11, 0x12, 0x13 }, deserialise(new StaticBytesTypeConverter(4),
				toArray("1011121300000000000000000000000000000000000000000000000000000000")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void deserialiseBytes4IllegalPadding() {
		deserialise(new StaticBytesTypeConverter(4),
				toArray("1011121300000000000000000000000000000000000000000000000000000001"));
	}

	@Test
	public void serialiseBytes32() {
		assertArrayEquals(toArray("1011121310111213101112131011121310111213101112131011121310111213"),
				serialise(new StaticBytesTypeConverter(32),
						new byte[] { 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11,
								0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13,
								0x10, 0x11, 0x12, 0x13 }));
	}

	@Test(expected = IllegalArgumentException.class)
	public void serialiseBytes32ArrayTooShort() {
		serialise(new StaticBytesTypeConverter(32), new byte[] {});
	}

	@Test(expected = IllegalArgumentException.class)
	public void serialiseBytes32ArrayTooLong() {
		serialise(new StaticBytesTypeConverter(32),
				new byte[] { 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12,
						0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12,
						0x13, 0x00 });
	}

	@Test
	public void deserialiseBytes32() {
		assertArrayEquals(
				new byte[] { 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12,
						0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12,
						0x13 },
				deserialise(new StaticBytesTypeConverter(32),
						toArray("1011121310111213101112131011121310111213101112131011121310111213")));
	}
}