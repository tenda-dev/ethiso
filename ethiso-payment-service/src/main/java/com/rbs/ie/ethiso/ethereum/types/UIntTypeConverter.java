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

import static com.rbs.ie.ethiso.ethereum.types.TypeConverterUtils.lpadZeroTo32;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class UIntTypeConverter implements TypeConverter<BigInteger> {

	private final int size;

	public UIntTypeConverter(final int size) {
		if (size <= 0 || size > 256 || size % 8 != 0) {
			throw new IllegalArgumentException("Invalid size " + size);
		}

		this.size = size;
	}

	@Override
	public void serialiseType(final ByteBuffer byteBuffer, final BigInteger value) {
		if (value.signum() < 0 || value.bitLength() > size) {
			throw new IllegalArgumentException("Value " + value + " not valid for type uint" + size);
		}

		lpadZeroTo32(byteBuffer, value.toByteArray());
	}

	@Override
	public BigInteger deserialise(final ByteBuffer byteBuffer) {
		final byte[] data = new byte[32];

		byteBuffer.get(data);

		final BigInteger bigInteger = new BigInteger(data);

		if (bigInteger.bitLength() > size) {
			throw new IllegalArgumentException("Value " + bigInteger + " not valid for type uint" + size);
		}

		return bigInteger;
	}

	@Override
	public Class<BigInteger> getTypeClass() {
		return BigInteger.class;
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