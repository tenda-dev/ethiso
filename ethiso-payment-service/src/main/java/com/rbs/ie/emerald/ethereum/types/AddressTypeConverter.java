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

import java.math.BigInteger;
import java.nio.ByteBuffer;

import com.rbs.ie.ethiso.ethereum.rpc.Address;

public class AddressTypeConverter implements TypeConverter<Address> {

	private static final TypeConverter<BigInteger> UINT160_TYPE_CONVERTER = new UIntTypeConverter(160);
	private static final int HEX_RADIX = 16;
	private static final String HEX_PREFIX = "0x";
	private static final String ZEROS = "0000000000000000000000000000000000000000";

	@Override
	public void serialiseType(final ByteBuffer byteBuffer, final Address value) {
		UINT160_TYPE_CONVERTER.serialiseType(byteBuffer,
				new BigInteger(value.toString().substring(HEX_PREFIX.length()), HEX_RADIX));
	}

	@Override
	public Address deserialise(final ByteBuffer byteBuffer) {
		return new Address(HEX_PREFIX + pad(UINT160_TYPE_CONVERTER.deserialise(byteBuffer).toString(HEX_RADIX)));
	}

	private static String pad(final String address) {
		return ZEROS.substring(address.length()) + address;
	}

	@Override
	public Class<Address> getTypeClass() {
		return Address.class;
	}
	
	@Override
	public boolean isDynamic() {
		return false;
	}
	
	@Override
	public int getHeadSize() {
		return 32;
	}
}