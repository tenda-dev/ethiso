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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class TypeConverterManagerImpl implements TypeConverterManager {

	private static final Iterable<TypeConverterFactory<?>> TYPE_CONVERTER_FACTORIES = getTypeConverterFactories();
	private final Map<String, TypeConverter<?>> typeConverters = new HashMap<>();

	/* (non-Javadoc)
	 * @see com.std.ie.ethiso.ethereum.types.TypeConverterManager#getTypeConverter(java.lang.String)
	 */
	@Override
	public TypeConverter<?> getTypeConverter(final String type) {
		TypeConverter<?> typeConverter = typeConverters.get(type);

		if (typeConverter == null) {
			typeConverter = newTypeConverter(type);

			if (typeConverter == null) {
				throw new IllegalArgumentException("Unsupported type " + type);
			}

			typeConverters.put(type, typeConverter);
		}

		return typeConverter;
	}

	private TypeConverter<?> newTypeConverter(final String type) {
		TypeConverter<?> typeConverter = null;

		final Iterator<TypeConverterFactory<?>> factoryIterator = TYPE_CONVERTER_FACTORIES.iterator();

		while (typeConverter == null && factoryIterator.hasNext()) {
			final TypeConverterFactory<?> factory = factoryIterator.next();

			if (factory.supportsType(type)) {
				typeConverter = factory.newTypeConverter(this, type);
			}
		}

		if (typeConverter == null) {
			throw new IllegalArgumentException("Unsupported type [" + type + "]");
		}

		return typeConverter;
	}

	private static Iterable<TypeConverterFactory<?>> getTypeConverterFactories() {
		final List<TypeConverterFactory<?>> typeConverterFactories = new ArrayList<>();

		for (final TypeConverterFactory<?> typeConverterFactory : ServiceLoader.load(TypeConverterFactory.class)) {
			typeConverterFactories.add(typeConverterFactory);
		}

		return typeConverterFactories;
	}
}