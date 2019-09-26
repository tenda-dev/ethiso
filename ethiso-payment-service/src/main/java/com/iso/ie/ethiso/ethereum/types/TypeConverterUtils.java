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

import java.nio.ByteBuffer;

public class TypeConverterUtils {

	private static final byte[] ZEROS = { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
	private static final byte[] FFS = { -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1,
			-0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1,
			-0x1 };

	private TypeConverterUtils() {
		// Prevent instantiation
	}

	static void lpadZeroTo32(final ByteBuffer byteBuffer, final byte[] bytes) {
		lpadTo32(byteBuffer, bytes, ZEROS);
	}

	static void lpadFFTo32(final ByteBuffer byteBuffer, final byte[] bytes) {
		lpadTo32(byteBuffer, bytes, FFS);
	}

	static void rpadZeroTo32(final ByteBuffer byteBuffer, final byte[] bytes) {
		rpadTo32(byteBuffer, bytes, ZEROS);
	}

	private static void lpadTo32(final ByteBuffer byteBuffer, final byte[] bytes, final byte[] pad) {
		final int offset = leadingZeros(bytes);

		byteBuffer.put(pad, 0, pad.length - (bytes.length - offset));
		byteBuffer.put(bytes, offset, bytes.length - offset);
	}

	private static void rpadTo32(final ByteBuffer byteBuffer, final byte[] bytes, final byte[] pad) {
		byteBuffer.put(bytes, 0, bytes.length);
		
		if (bytes.length % 32 != 0) {
			int padSize = 32 - bytes.length % 32;
			
			byteBuffer.put(pad, 0, padSize);
		}
	}

	private static int leadingZeros(final byte[] bytes) {
		int offset = 0;

		while (offset < bytes.length - 1 && bytes[offset] == 0) {
			offset++;
		}

		return offset;
	}
}