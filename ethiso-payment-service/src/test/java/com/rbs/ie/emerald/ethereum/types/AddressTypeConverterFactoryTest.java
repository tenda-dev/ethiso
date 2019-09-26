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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.rbs.ie.emerald.ethereum.rpc.Address;

public class AddressTypeConverterFactoryTest {

	@Test
	public void address() {
		assertNotNull(getTypeConverter("address"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void addressDynamicArray() {
		getTypeConverter("address[]");
	}

	@Test(expected = IllegalArgumentException.class)
	public void addressStaticArrayFails() {
		getTypeConverter("address[2]");
	}

	private TypeConverter<Address> getTypeConverter(final String type) {
		return new AddressTypeConverterFactory().newTypeConverter(null, type);
	}
}