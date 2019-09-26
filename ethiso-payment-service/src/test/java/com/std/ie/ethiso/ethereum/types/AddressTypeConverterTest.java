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
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.std.ie.ethiso.ethereum.rpc.Address;

public class AddressTypeConverterTest {

	@Test
	public void serialiseAddress() {
		assertArrayEquals(toArray("0000000000000000000000000123456789012345678901234567890123456789"),
				serialise(new AddressTypeConverter(), new Address("0x0123456789012345678901234567890123456789")));
	}

	@Test
	public void deserialiseAddress() {
		assertEquals(new Address("0x0123456789012345678901234567890123456789"), deserialise(new AddressTypeConverter(),
				toArray("0000000000000000000000000123456789012345678901234567890123456789")));
	}
}