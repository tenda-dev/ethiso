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
package com.iso.ie.ethiso.ethereum.types;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class TypeConverterTestUtils {

	private TypeConverterTestUtils() {
		// Prevent instantiation
	}

	static <T> byte[] serialise(final TypeConverter<T> typeConverter, final T value) {
		final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

		typeConverter.serialiseType(byteBuffer, value);
		
		byteBuffer.flip();
		
		final byte[] data = new byte[byteBuffer.limit()];
		
		byteBuffer.get(data);
		
		return data;
	}

	static <T> T deserialise(final TypeConverter<T> typeConverter, final byte[] value) {
		final ByteBuffer byteBuffer = ByteBuffer.wrap(value);

		final T result = typeConverter.deserialise(byteBuffer);
		
		if (byteBuffer.hasRemaining()) {
			throw new IllegalArgumentException("Buffer not exhausted");
		}
		
		return result;
	}

	static <T> void checkSerialisationBounds(final TypeConverter<T> typeConverter, final T valid, final T invalid) {
		serialise(typeConverter, valid);

		try {
			serialise(typeConverter, invalid);

			fail("Serialisation should fail [" + typeConverter.getClass().getSimpleName() + ", " + invalid + "]");
		} catch (final IllegalArgumentException e) {
			// Expected to fail
		}
	}

	static void checkDeserialisationBounds(final TypeConverter<?> typeConverter, final byte[] valid,
			final byte[] invalid) {
		deserialise(typeConverter, valid);

		try {
			deserialise(typeConverter, invalid);

			fail("Deserialisation should fail [" + typeConverter.getClass().getSimpleName() + ", "
					+ Arrays.toString(invalid) + "]");
		} catch (final IllegalArgumentException e) {
			// Expected to fail
		}
	}

	static byte[] toArray(final String expected) {
		final byte[] data = new byte[expected.length() / 2];

		for (int idx = 0; idx < data.length; idx++) {
			data[idx] = (byte) Integer.parseInt(expected.substring(idx * 2, idx * 2 + 2), 16);
		}

		return data;
	}
}