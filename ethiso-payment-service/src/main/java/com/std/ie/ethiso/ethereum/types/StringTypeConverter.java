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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StringTypeConverter implements TypeConverter<String> {

	private static final TypeConverter<byte[]> BYTES_TYPE_CONVERTER = new DynamicBytesTypeConverter();
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	@Override
	public void serialiseType(final ByteBuffer byteBuffer, final String value) {
		if (value == null) {
			throw new IllegalArgumentException("Value " + value + " not supported");
		}

		BYTES_TYPE_CONVERTER.serialiseType(byteBuffer, value.getBytes(UTF8_CHARSET));
	}

	@Override
	public String deserialise(final ByteBuffer byteBuffer) {
		return new String(BYTES_TYPE_CONVERTER.deserialise(byteBuffer), UTF8_CHARSET);
	}

	@Override
	public Class<String> getTypeClass() {
		return String.class;
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