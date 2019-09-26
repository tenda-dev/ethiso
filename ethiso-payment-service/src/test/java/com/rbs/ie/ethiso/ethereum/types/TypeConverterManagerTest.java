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

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

public class TypeConverterManagerTest {

	private static TypeConverterManager typeConverterManager;

	@BeforeClass
	public static void instantiateTypeConverterManager() {
		typeConverterManager = new TypeConverterManagerImpl();
	}

	@Test
	public void addressTypeConverterRegistered() {
		assertNotNull(typeConverterManager.getTypeConverter("address"));
	}

	@Test
	public void boolTypeConverterRegistered() {
		assertNotNull(typeConverterManager.getTypeConverter("bool"));
	}

	@Test
	public void bytesTypeConverterRegistered() {
		assertNotNull(typeConverterManager.getTypeConverter("bytes"));
	}

	@Test
	public void bytesMTypeConverterRegistered() {
		assertNotNull(typeConverterManager.getTypeConverter("bytes8"));
	}

	@Test
	public void fixedTypeConverterRegistered() {
		assertNotNull(typeConverterManager.getTypeConverter("fixed"));
	}

	@Test
	public void intTypeConverterRegistered() {
		assertNotNull(typeConverterManager.getTypeConverter("int"));
	}

	@Test
	public void stringTypeConverterRegistered() {
		assertNotNull(typeConverterManager.getTypeConverter("string"));
	}

	@Test
	public void ufixedTypeConverterRegistered() {
		assertNotNull(typeConverterManager.getTypeConverter("ufixed"));
	}

	@Test
	public void uintTypeConverterRegistered() {
		assertNotNull(typeConverterManager.getTypeConverter("uint"));
	}

	@Test
	public void fixedArrayTypeConverterRegistered() {
		assertNotNull(typeConverterManager.getTypeConverter("uint[2]"));
	}

	@Test
	public void dynamicArrayTypeConverterRegistered() {
		assertNotNull(typeConverterManager.getTypeConverter("uint[]"));
	}
}