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

import java.math.BigInteger;

public class IntTypeConverterFactory implements TypeConverterFactory<BigInteger> {

	private static final int MIN_SIZE_IN_BITS = 8;
	private static final int STEP_SIZE_IN_BITS = 8;
	private static final int MAX_SIZE_IN_BITS = 256;
	private static final String INT = "int";
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
	public TypeConverter<BigInteger> newTypeConverter(final TypeConverterManager typeConverterManager,
			final String type) {
		return new UIntTypeConverter(getSize(type));
	}

	private static int getSize(final String type) {
		if (!type.startsWith(INT)) {
			throw new IllegalArgumentException("Type not supported [" + type + "]");
		}

		final String suffix = type.substring(INT.length());

		if (suffix.startsWith(PLUS)) {
			throw new IllegalArgumentException("Invalid uint size [" + suffix + "]");
		}

		final int size = suffix.isEmpty() ? MAX_SIZE_IN_BITS : Integer.parseUnsignedInt(suffix);

		if (size < MIN_SIZE_IN_BITS || size > MAX_SIZE_IN_BITS || size % STEP_SIZE_IN_BITS != 0) {
			throw new IllegalArgumentException("Invalid uint size [" + suffix + "]");
		}

		return size;
	}
}