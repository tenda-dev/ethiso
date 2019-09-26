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

public class StaticArrayTypeConverterFactory<T> implements TypeConverterFactory<T[]> {

	@Override
	public boolean supportsType(final String type) {
		boolean supportsType = false;

		try {
			getFirstFixedArraySize(type);

			supportsType = true;
		} catch (final IllegalArgumentException e) {
			// Not supported
		}

		return supportsType;
	}

	@Override
	public TypeConverter<T[]> newTypeConverter(final TypeConverterManager typeConverterManager, final String type) {
		final int size = getFirstFixedArraySize(type);

		@SuppressWarnings("unchecked")
		final TypeConverter<T> componentTypeConverter = (TypeConverter<T>) typeConverterManager
				.getTypeConverter(getComponentType(type));

		return new StaticArrayTypeConverter<>(componentTypeConverter, componentTypeConverter.getTypeClass(), size);
	}

	private int getFirstFixedArraySize(final String type) {
		final int startIdx = type.indexOf('[');
		final int endIdx = type.indexOf(']');

		if (startIdx == -1 || endIdx == -1 || endIdx <= startIdx + 1) {
			throw new IllegalArgumentException("Type not supported [" + type + "]");
		}

		final int size = Integer.parseInt(type.substring(startIdx + 1, endIdx));

		if (size <= 0) {
			throw new IllegalArgumentException("Type not supported [" + type + "]");
		}

		return size;
	}

	private String getComponentType(final String type) {
		final int startIdx = type.indexOf('[');
		final int endIdx = type.indexOf(']');

		return type.substring(0, startIdx) + type.substring(endIdx + 1);
	}
}