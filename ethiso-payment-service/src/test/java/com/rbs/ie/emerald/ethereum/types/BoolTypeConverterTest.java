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
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BoolTypeConverterTest {

	@Test
	public void serialiseTrue() {
		assertArrayEquals(toArray("0000000000000000000000000000000000000000000000000000000000000001"),
				serialise(new BoolTypeConverter(), TRUE));
	}

	@Test
	public void serialiseFalse() {
		assertArrayEquals(toArray("0000000000000000000000000000000000000000000000000000000000000000"),
				serialise(new BoolTypeConverter(), FALSE));
	}

	@Test
	public void deserialiseTrue() {
		assertEquals(TRUE, deserialise(new BoolTypeConverter(),
				toArray("0000000000000000000000000000000000000000000000000000000000000001")));
	}

	@Test
	public void deserialiseFalse() {
		assertEquals(FALSE, deserialise(new BoolTypeConverter(),
				toArray("0000000000000000000000000000000000000000000000000000000000000000")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void deserialiseInvalidValue() {
		assertEquals(FALSE, deserialise(new BoolTypeConverter(),
				toArray("0000000000000000000000000000000000000000000000000000000000000002")));
	}
}