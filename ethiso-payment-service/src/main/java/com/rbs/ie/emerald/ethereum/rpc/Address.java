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

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Address implements CharSequence {

	private static final String ZERO_ADDRESS = "0x0000000000000000000000000000000000000000";

	private static final Pattern PATTERN = Pattern.compile("0x[0-9a-fA-F]{40}");
	private String address;

	public Address(final String address) {
		if (address != null && !PATTERN.matcher(address).matches()) {
			throw new IllegalArgumentException("Invalid address format [" + address + "]");
		}

		this.address = address != null ? address : ZERO_ADDRESS;
	}

	@Override
	public char charAt(int index) {
		return address.charAt(index);
	}

	@Override
	public int length() {
		return address.length();
	}

	@Override
	public CharSequence subSequence(final int start, final int end) {
		return address.subSequence(start, end);
	}

	@JsonIgnore(true)
	public boolean isZero() {
		return ZERO_ADDRESS.equals(address);
	}

	@Override
	public boolean equals(final Object that) {
		boolean equals = false;

		if (this == that) {
			equals = true;
		} else if (that instanceof Address) {
			equals = ((Address) that).address.equals(address);
		}

		return equals;
	}

	@Override
	public int hashCode() {
		return address.hashCode();
	}

	@Override
	public String toString() {
		return address;
	}
	
	/**
	 * When filtering we need a full 32 byte address converted to a string
	 */
	public String toWideAddressString() {
		return ZERO_ADDRESS.substring(0, 26) + address.substring(2);
	}
}