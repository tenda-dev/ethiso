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

public class StaticBytesTypeConverterFactory implements TypeConverterFactory<byte[]> {

	private static final int MAX_SIZE = 32;
	private static final String BYTES = "bytes";
	private static final String PLUS = "+";

	@Override
	public boolean supportsType(final String type) {
		boolean supported = false;

		try {
			getSize(type);

			supported = true;
		} catch (final IllegalArgumentException e) {
			// Unsupported size or type
		}

		return supported;
	}

	@Override
	public TypeConverter<byte[]> newTypeConverter(final TypeConverterManager typeConverterManager, final String type) {
		return new StaticBytesTypeConverter(getSize(type));
	}

	private static int getSize(final String type) {
		if (!type.startsWith(BYTES)) {
			throw new IllegalArgumentException("Type not supported [" + type + "]");
		}

		final String suffix = type.substring(BYTES.length());

		if (suffix.startsWith(PLUS)) {
			throw new IllegalArgumentException("Invalid bytes size [" + suffix + "]");
		}

		if (suffix.isEmpty()) {
			throw new IllegalArgumentException("Dynamic bytes arrays not supported [" + suffix + "]");
		}
		
		final int size = Integer.parseUnsignedInt(suffix);

		if (size < 0 || size > MAX_SIZE) {
			throw new IllegalArgumentException("Invalid bytes size [" + suffix + "]");
		}

		return size;
	}
}