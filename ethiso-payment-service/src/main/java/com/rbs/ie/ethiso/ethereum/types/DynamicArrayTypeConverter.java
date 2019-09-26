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

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class DynamicArrayTypeConverter<T> implements TypeConverter<T[]> {

	private static final TypeConverter<BigInteger> SIZE_TYPE_CONVERTER = new UIntTypeConverter(256);
	
	private final TypeConverter<T> componentTypeConverter;
	private final Class<T> componentType;

	public DynamicArrayTypeConverter(final TypeConverter<T> componentTypeConverter, final Class<T> componentType) {
		if (componentTypeConverter == null || componentType == null) {
			throw new IllegalArgumentException();
		}

		this.componentTypeConverter = componentTypeConverter;
		this.componentType = componentType;
	}

	@Override
	public void serialiseType(final ByteBuffer byteBuffer, final T[] value) {
		SIZE_TYPE_CONVERTER.serialiseType(byteBuffer, new BigInteger(String.valueOf(value.length)));

		for (final T component : value) {
			componentTypeConverter.serialiseType(byteBuffer, component);
		}
	}

	@Override
	public T[] deserialise(final ByteBuffer byteBuffer) {
		final int size = SIZE_TYPE_CONVERTER.deserialise(byteBuffer).intValue();
		
		@SuppressWarnings("unchecked")
		final T[] value = (T[]) Array.newInstance(componentType, size);

		for (int i = 0; i < size; i++) {
			value[i] = componentTypeConverter.deserialise(byteBuffer);
		}

		return value;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<T[]> getTypeClass() {
		return (Class<T[]>) Array.newInstance(componentType, 0).getClass();
	}
	
	@Override
	public boolean isDynamic() {
		return true;
	}
	
	@Override
	public int getHeadSize() {
		return 32;
	}
}