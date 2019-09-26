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

import static com.rbs.ie.emerald.ethereum.types.TypeConverterTestUtils.deserialise;
import static com.rbs.ie.emerald.ethereum.types.TypeConverterTestUtils.serialise;
import static com.rbs.ie.emerald.ethereum.types.TypeConverterTestUtils.toArray;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringTypeConverterTest {

	@Test
	public void serialiseString() {
		assertArrayEquals(
				toArray("0000000000000000000000000000000000000000000000000000000000000004"
						+ "7465737400000000000000000000000000000000000000000000000000000000"),
				serialise(new StringTypeConverter(), "test"));
	}

	@Test
	public void serialiseEmptyString() {
		assertArrayEquals(toArray("0000000000000000000000000000000000000000000000000000000000000000"),
				serialise(new StringTypeConverter(), ""));
	}

	@Test
	public void deserialiseString() {
		assertEquals("test",
				deserialise(new StringTypeConverter(),
						toArray("0000000000000000000000000000000000000000000000000000000000000004"
								+ "7465737400000000000000000000000000000000000000000000000000000000")));
	}

	@Test
	public void deserialiseEmptyString() {
		assertEquals("", deserialise(new StringTypeConverter(),
				toArray("0000000000000000000000000000000000000000000000000000000000000000")));
	}
}