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
package com.rbs.ie.ethiso.ethereum.rpc;

import java.math.BigInteger;

public class Quantity extends BigInteger implements CharSequence {

	private static final long serialVersionUID = 1L;
	private static final int HEX = 16;
	private static final String PREFIX = "0x";

	private String stringValue;

	public Quantity(final String val) {
		super(stripPrefix(val), HEX);
	}

	@Override
	public int length() {
		return toString().length();
	}

	@Override
	public char charAt(final int index) {
		return toString().charAt(index);
	}

	@Override
	public CharSequence subSequence(final int start, final int end) {
		return toString().subSequence(start, end);
	}

	@Override
	public String toString() {
		if (stringValue == null) {
			stringValue = PREFIX + toString(HEX);
		}

		return stringValue;
	}
	
	private static String stripPrefix(final String value) {
		if (!value.startsWith(PREFIX)) {
			throw new IllegalArgumentException("Value doesn't start with " + PREFIX + ", " + value);
		}
		
		return value.substring(PREFIX.length());
	}
}