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
package com.rbs.ie.jsonrpc.http;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;

import org.junit.Test;

import spark.Service;

public class HttpJsonRpcTransportTest {
	private static final String PATH1 = "/1";
	private static final String PATH2 = "/2";

	@Test
	public void checkTwoTransportsOnSamePortSharedSparkServiceInstance() {
		final InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", 4567);

		try (final HttpJsonRpcTransport transport1 = new HttpJsonRpcTransport(address, PATH1);
				final HttpJsonRpcTransport transport2 = new HttpJsonRpcTransport(address, PATH2)) {
			assertEquals(true, transport1.getService() == transport2.getService());
		}
	}

	@Test
	public void checkZeroReferenceCountClosesSharedSparkServiceInstance()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		final InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", 4567);

		final Service originalService;

		try (final HttpJsonRpcTransport transport = new HttpJsonRpcTransport(address, PATH1)) {
			originalService = transport.getService();
		}

		final Field initialisedField = Service.class.getDeclaredField("initialized");

		initialisedField.setAccessible(true);

		assertEquals(false, initialisedField.get(originalService));

		try (final HttpJsonRpcTransport newTransport = new HttpJsonRpcTransport(address, PATH1)) {
			assertEquals(true, originalService != newTransport.getService());
		}
	}
}