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

public class DynamicArrayTypeConverterFactory<T> implements TypeConverterFactory<T[]> {

	@Override
	public boolean supportsType(final String type) {
		return isFirstArraySizeDynamic(type);
	}

	@Override
	public TypeConverter<T[]> newTypeConverter(final TypeConverterManager typeConverterManager, final String type) {
		if (!supportsType(type)) {
			throw new IllegalArgumentException("Type not supported [" + type + "]");
		}

		@SuppressWarnings("unchecked")
		final TypeConverter<T> componentTypeConverter = (TypeConverter<T>) typeConverterManager
				.getTypeConverter(getComponentType(type));

		return new DynamicArrayTypeConverter<>(componentTypeConverter, componentTypeConverter.getTypeClass());
	}

	private boolean isFirstArraySizeDynamic(final String type) {
		final int startIdx = type.indexOf('[');

		return startIdx != -1 && type.charAt(startIdx + 1) == ']';
	}

	private String getComponentType(final String type) {
		final int startIdx = type.indexOf('[');

		return type.substring(0, startIdx) + type.substring(startIdx + 2);
	}
}