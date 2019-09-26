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

public class BoolTypeConverterFactory implements TypeConverterFactory<Boolean> {

	private static final String BOOL = "bool";
	private static final TypeConverter<Boolean> INSTANCE = new BoolTypeConverter();

	@Override
	public boolean supportsType(final String type) {
		return type.equals(BOOL);
	}

	@Override
	public TypeConverter<Boolean> newTypeConverter(final TypeConverterManager typeConverterManager, final String type) {
		if (!supportsType(type)) {
			throw new IllegalArgumentException("Type not supported [" + type + "]");
		}

		return INSTANCE;
	}
}