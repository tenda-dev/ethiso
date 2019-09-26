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

import static com.rbs.ie.ethiso.ethereum.types.TypeConverterTestUtils.deserialise;
import static com.rbs.ie.ethiso.ethereum.types.TypeConverterTestUtils.serialise;
import static com.rbs.ie.ethiso.ethereum.types.TypeConverterTestUtils.toArray;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class DynamicBytesTypeConverterTest {

	@Test
	public void serialiseBytesOfLength0() {
		assertArrayEquals(toArray("0000000000000000000000000000000000000000000000000000000000000000"),
				serialise(new DynamicBytesTypeConverter(), new byte[] {}));
	}

	@Test
	public void serialiseBytesOfLength1() {
		assertArrayEquals(
				toArray("0000000000000000000000000000000000000000000000000000000000000001"
						+ "1000000000000000000000000000000000000000000000000000000000000000"),
				serialise(new DynamicBytesTypeConverter(), new byte[] { 0x10 }));
	}

	@Test
	public void serialiseBytesOfLength32() {
		assertArrayEquals(
				toArray("0000000000000000000000000000000000000000000000000000000000000020"
						+ "1011121310111213101112131011121310111213101112131011121310111213"),
				serialise(new DynamicBytesTypeConverter(),
						new byte[] { 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11,
								0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13,
								0x10, 0x11, 0x12, 0x13 }));
	}

	@Test
	public void serialiseBytesOfLength33() {
		assertArrayEquals(
				toArray("0000000000000000000000000000000000000000000000000000000000000021"
						+ "1011121310111213101112131011121310111213101112131011121310111213"
						+ "1000000000000000000000000000000000000000000000000000000000000000"),
				serialise(new DynamicBytesTypeConverter(),
						new byte[] { 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11,
								0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13,
								0x10, 0x11, 0x12, 0x13, 0x10 }));
	}

	@Test
	public void deserialiseBytesOfLength0() {
		assertArrayEquals(new byte[0], deserialise(new DynamicBytesTypeConverter(),
				toArray("0000000000000000000000000000000000000000000000000000000000000000")));
	}

	@Test
	public void deserialiseBytesOfLength1() {
		assertArrayEquals(new byte[] { 0x10 },
				deserialise(new DynamicBytesTypeConverter(),
						toArray("0000000000000000000000000000000000000000000000000000000000000001"
								+ "1000000000000000000000000000000000000000000000000000000000000000")));
	}

	@Test
	public void deserialiseBytesOfLength32() {
		assertArrayEquals(
				new byte[] { 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12,
						0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12,
						0x13 },
				deserialise(new DynamicBytesTypeConverter(),
						toArray("0000000000000000000000000000000000000000000000000000000000000020"
								+ "1011121310111213101112131011121310111213101112131011121310111213")));
	}

	@Test
	public void deserialiseBytesOfLength33() {
		assertArrayEquals(
				new byte[] { 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12,
						0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12, 0x13, 0x10, 0x11, 0x12,
						0x13, 0x10 },
				deserialise(new DynamicBytesTypeConverter(),
						toArray("0000000000000000000000000000000000000000000000000000000000000021"
								+ "1011121310111213101112131011121310111213101112131011121310111213"
								+ "1000000000000000000000000000000000000000000000000000000000000000")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void deserialiseBytesOfLength1IllegalPaddingFails() {
		deserialise(new DynamicBytesTypeConverter(),
				toArray("0000000000000000000000000000000000000000000000000000000000000001"
						+ "0000000000000000000000000000000000000000000000000000000000000001"));
	}
}