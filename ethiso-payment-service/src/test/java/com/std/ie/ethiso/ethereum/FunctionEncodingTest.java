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
package com.std.ie.ethiso.ethereum;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.std.ie.ethiso.ethereum.rpc.Function;
import com.std.ie.ethiso.ethereum.rpc.Parameter;

public class FunctionEncodingTest {

	/**
	 * @see https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI#examples
	 */
	@Test
	public void ethereumContractABITestCase1() {
		final Function function = new Function();

		function.setType("function");
		function.setName("baz");
		function.setInputs(asList(type("uint32"), type("bool")));

		final FunctionEncoding functionEncoding = new FunctionEncoding(function);

		assertEquals(
				"0xcdcd77c" + "00000000000000000000000000000000000000000000000000000000000000045"
						+ "0000000000000000000000000000000000000000000000000000000000000001",
				functionEncoding.encode(new BigInteger("69"), true));
	}

	/**
	 * @see https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI#examples
	 */
	@Test
	public void ethereumContractABITestCase2() {
		final Function function = new Function();

		function.setType("function");
		function.setName("bar");
		function.setInputs(asList(type("fixed128x128[2]")));

		final FunctionEncoding functionEncoding = new FunctionEncoding(function);

		assertEquals(
				"0xab55044d" + "0000000000000000000000000000000220000000000000000000000000000000"
						+ "0000000000000000000000000000000880000000000000000000000000000000",
				functionEncoding
						.encode(new Object[] { new BigDecimal[] { new BigDecimal("2.125"), new BigDecimal("8.5") } }));
	}

	/**
	 * @see https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI#examples
	 */
	@Test
	public void ethereumContractABITestCase3() {
		final Function function = new Function();

		function.setType("function");
		function.setName("sam");
		function.setInputs(asList(type("bytes"), type("bool"), type("uint256[]")));

		final FunctionEncoding functionEncoding = new FunctionEncoding(function);

		assertEquals(
				"0xa5643bf2" + "0000000000000000000000000000000000000000000000000000000000000060"
						+ "0000000000000000000000000000000000000000000000000000000000000001"
						+ "00000000000000000000000000000000000000000000000000000000000000a0"
						+ "0000000000000000000000000000000000000000000000000000000000000004"
						+ "6461766500000000000000000000000000000000000000000000000000000000"
						+ "0000000000000000000000000000000000000000000000000000000000000003"
						+ "0000000000000000000000000000000000000000000000000000000000000001"
						+ "0000000000000000000000000000000000000000000000000000000000000002"
						+ "0000000000000000000000000000000000000000000000000000000000000003",
				functionEncoding.encode("dave".getBytes(), true,
						new BigInteger[] { new BigInteger("1"), new BigInteger("2"), new BigInteger("3") }));
	}

	/**
	 * @see https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI#use-of-
	 *      dynamic-types
	 */
	@Test
	public void ethereumContractABIDynamicTestCase() {
		final Function function = new Function();

		function.setType("function");
		function.setName("f");
		function.setInputs(asList(type("uint256"), type("uint32[]"), type("bytes10"), type("bytes")));

		final FunctionEncoding functionEncoding = new FunctionEncoding(function);

		assertEquals(
				"0x8be65246" + "0000000000000000000000000000000000000000000000000000000000000123"
						+ "0000000000000000000000000000000000000000000000000000000000000080"
						+ "3132333435363738393000000000000000000000000000000000000000000000"
						+ "00000000000000000000000000000000000000000000000000000000000000e0"
						+ "0000000000000000000000000000000000000000000000000000000000000002"
						+ "0000000000000000000000000000000000000000000000000000000000000456"
						+ "0000000000000000000000000000000000000000000000000000000000000789"
						+ "000000000000000000000000000000000000000000000000000000000000000d"
						+ "48656c6c6f2c20776f726c642100000000000000000000000000000000000000",
				functionEncoding.encode(new BigInteger("123", 16),
						new BigInteger[] { new BigInteger("456", 16), new BigInteger("789", 16) },
						"1234567890".getBytes(), "Hello, world!".getBytes()));
	}

	private static Parameter type(final String type) {
		final Parameter parameter = new Parameter();

		parameter.setType(type);

		return parameter;
	}
}