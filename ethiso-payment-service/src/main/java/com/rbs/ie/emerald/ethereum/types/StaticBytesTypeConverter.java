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

import static com.rbs.ie.emerald.ethereum.types.TypeConverterUtils.rpadZeroTo32;

import java.nio.ByteBuffer;

public class StaticBytesTypeConverter implements TypeConverter<byte[]> {

	private final int size;

	public StaticBytesTypeConverter(final int size) {
		if (size <= 0 || size > 32) {
			throw new IllegalArgumentException("Invalid size " + size);
		}

		this.size = size;
	}

	@Override
	public void serialiseType(final ByteBuffer byteBuffer, final byte[] value) {
		if (value.length != size) {
			throw new IllegalArgumentException("Value length " + value.length + " not valid for type bytes" + size);
		}

		rpadZeroTo32(byteBuffer, value);
	}

	@Override
	public byte[] deserialise(final ByteBuffer byteBuffer) {
		final byte[] data = new byte[size];

		byteBuffer.get(data);

		final byte[] pad = new byte[32 - size];

		byteBuffer.get(pad);

		for (int i = 0; i < pad.length; i++) {
			if (pad[i] != 0) {
				throw new IllegalArgumentException(
						"Bad padding at index " + (pad.length + i) + " value " + pad[i] + " for bytes " + size);
			}
		}

		return data;
	}

	@Override
	public Class<byte[]> getTypeClass() {
		return byte[].class;
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