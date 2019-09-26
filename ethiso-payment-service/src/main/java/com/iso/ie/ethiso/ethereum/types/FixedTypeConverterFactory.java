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

import java.math.BigDecimal;
import java.util.Arrays;

public class FixedTypeConverterFactory implements TypeConverterFactory<BigDecimal> {

	private static final int MIN_SIZE_IN_BITS = 8;
	private static final int STEP_SIZE_IN_BITS = 8;
	private static final int MAX_SIZE_IN_BITS = 256;
	private static final String FIXED = "fixed";
	private static final String PLUS = "+";
	private static final int[] DEFAULT_SIZES = { 128, 128 };

	@Override
	public boolean supportsType(final String type) {
		boolean supported = false;

		try {
			getSizes(type);

			supported = true;
		} catch (final IllegalArgumentException e) {
			// Unsupported size or type
		}

		return supported;
	}

	@Override
	public TypeConverter<BigDecimal> newTypeConverter(final TypeConverterManager typeConverterManager,
			final String type) {
		final int[] sizes = getSizes(type);

		return new FixedTypeConverter(sizes[0], sizes[1]);
	}

	private static int[] getSizes(final String type) {
		if (!type.startsWith(FIXED)) {
			throw new IllegalArgumentException("Type not supported [" + type + "]");
		}

		final String suffix = type.substring(FIXED.length());

		return suffix.isEmpty() ? DEFAULT_SIZES : getSizes(suffix.split("x"));
	}

	private static int[] getSizes(final String[] sizeStrings) {
		if (sizeStrings.length != 2) {
			throw new IllegalArgumentException("Bad fixed sizes " + Arrays.toString(sizeStrings));
		}

		final int[] sizes = new int[2];

		for (int i = 0; i < sizes.length; i++) {
			if (sizeStrings[i].startsWith(PLUS)) {
				throw new IllegalArgumentException("Invalid uint size [" + sizeStrings[i] + "]");
			}

			sizes[i] = Integer.parseInt(sizeStrings[i]);

			if (sizes[i] < MIN_SIZE_IN_BITS || sizes[i] > MAX_SIZE_IN_BITS || sizes[i] % STEP_SIZE_IN_BITS != 0) {
				throw new IllegalArgumentException("Invalid fixed size [" + sizeStrings[i] + "]");
			}
		}

		return sizes;
	}
}