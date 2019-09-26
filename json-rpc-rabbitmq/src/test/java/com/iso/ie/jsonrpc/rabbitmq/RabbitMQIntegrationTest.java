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
package com.iso.ie.jsonrpc.rabbitmq;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rabbitmq.client.ConnectionFactory;
import com.iso.ie.jsonrpc.JsonRpcFactory;

public class RabbitMQIntegrationTest {

	private static final String QPID_CONFIG_JSON = "/qpid-config.json";
	private static final String QPID = "qpid";
	private static final String QPID_PASS_FILE_DEFAULT = "/passwd.properties";
	private static final String QPID_WORK_DIR = "qpid.work_dir";
	private static final String QPID_PASS_FILE = "qpid.pass_file";
	private static final String QPID_AMQP_PORT = "qpid.amqp_port";

	private static String ADDRESS = "sum";

	private static final String TEST_VALUE_LOWER = "test";
	private static final String TEST_VALUE_UPPER = "TEST";
	private static final String TEST_VALUE_FRIDAY_FRENCH = "vendredi";
	private static final String TEST_VALUE_EMPTY_STRING = "";
	private static final String TEST_VALUE_INVALID = "bad";

	public static final OutputStream DEV_NULL = new OutputStream() {
		public void write(int b) {
		}
	};

	private static Broker broker;
	private static int port = getFreePort();

	private TestInterface testInterface;

	private RabbitMQJsonRpcTransport transport;

	public interface TestInterface {
		void testMethod(String s, Date d);

		String testReturnMethod(BigInteger b, Calendar c);

		String callCallbackWith(String input);

		void registerCallback(Function<String, String> f);
	}

	@BeforeClass
	public static void startBroker() throws Exception {
		final String tempDir = Files.createTempDirectory(QPID).toAbsolutePath().toString();

		System.setProperty("derby.stream.error.file", tempDir + "/derby.log");

		broker = new Broker();

		final BrokerOptions brokerOptions = new BrokerOptions();

		brokerOptions.setConfigProperty(QPID_AMQP_PORT, String.valueOf(port));
		brokerOptions.setConfigProperty(QPID_PASS_FILE, getPathFromResource(QPID_PASS_FILE_DEFAULT));

		brokerOptions.setConfigProperty(QPID_WORK_DIR, tempDir);
		brokerOptions
				.setInitialConfigurationLocation(getPathFromResource(QPID_CONFIG_JSON));

		broker.startup(brokerOptions);
	}

	@Before
	public void setUp() throws Exception {

		final ConnectionFactory connectionFactory = new ConnectionFactory();

		connectionFactory.setPort(port);

		transport = new RabbitMQJsonRpcTransport(connectionFactory, ADDRESS);

		final JsonRpcFactory jsonRpcFactory = new JsonRpcFactory(transport);

		testInterface = jsonRpcFactory.newClient(TestInterface.class, 10, TimeUnit.HOURS);

		jsonRpcFactory.registerCallbackType(Function.class);

		jsonRpcFactory.registerService(TestInterface.class, new TestInterface() {
			Function<String, String> function;

			@Override
			public void testMethod(String s, Date d) throws IllegalArgumentException {
				if (s.equals(TEST_VALUE_INVALID)) {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public String testReturnMethod(BigInteger b, Calendar c) {
				c.set(Calendar.DAY_OF_WEEK, b.intValue());
				return c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG_FORMAT, Locale.FRENCH);
			}

			@Override
			public String callCallbackWith(String input) {
				return function.apply(input);
			}

			@Override
			public void registerCallback(Function<String, String> f) {
				this.function = f;
			}

		});
	}

	@Test
	public void testMethodSucceeds() {
		testInterface.testMethod(TEST_VALUE_EMPTY_STRING, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMethodFailsWithIllegalArgumentException() {
		testInterface.testMethod(TEST_VALUE_INVALID, null);
	}

	@Test
	public void testReturnMethodSucceeds() {
		assertEquals(TEST_VALUE_FRIDAY_FRENCH,
				testInterface.testReturnMethod(BigInteger.valueOf(6), Calendar.getInstance()));
	}

	@Test
	public void testCallback() {
		testInterface.registerCallback(String::toUpperCase);

		assertEquals(TEST_VALUE_UPPER, testInterface.callCallbackWith(TEST_VALUE_LOWER));
	}

	@After
	public void tearDown() {
		transport.close();
	}

	@AfterClass
	public static void stopBroker() {
		broker.shutdown();
	}

	private static int getFreePort() {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		} catch (final IOException e) {
			throw new IllegalStateException("Couldn't get a free port");
		}
	}

	public static String getPathFromResource(String resource) {
		try {
			return new File(RabbitMQIntegrationTest.class.getResource(resource).toURI()).getAbsolutePath();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("resource=[" + resource + "]");
		}
	}
}
