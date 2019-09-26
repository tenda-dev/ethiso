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
package com.std.ie.jsonrpc.http;

import com.std.ie.jsonrpc.JsonRpcFactory;
import com.std.ie.jsonrpc.JsonRpcTransport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpJsonRpcTransportIntegrationTest {


    private static final Logger LOG = LoggerFactory.getLogger(HttpJsonRpcTransportIntegrationTest.class);
    private static final InetSocketAddress address = new InetSocketAddress("localhost", 9898);
	private static final String PATH = "/";

    private TestInterface testInterface;

    public interface TestInterface {

        String testReturnMethod(BigInteger b, Calendar c);

    }

    @Before
    public void setup() {

        final JsonRpcTransport transport = new HttpJsonRpcTransport(address, PATH);
        final JsonRpcFactory jsonRpcFactory = new JsonRpcFactory(transport);

        testInterface = jsonRpcFactory.newClient(TestInterface.class, 10, TimeUnit.SECONDS);

        jsonRpcFactory.registerService(TestInterface.class, (b, c) -> {
            if (b.intValue() == 10) {
                throw new IllegalArgumentException();
            }
            c.set(Calendar.DAY_OF_WEEK, b.intValue());
            return c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG_FORMAT, Locale.FRENCH);
        });


    }

    @Test(expected = IllegalStateException.class)
    public void shouldPropagateTransportError() {
        final JsonRpcTransport transport = new HttpJsonRpcTransport(new InetSocketAddress("localhost", 9999), PATH);
        final JsonRpcFactory jsonRpcFactory = new JsonRpcFactory(transport);

        testInterface = jsonRpcFactory.newClient(TestInterface.class, 10, TimeUnit.SECONDS);

        testInterface.testReturnMethod(BigInteger.ONE, Calendar.getInstance());

    }

    @Test
    public void shouldBeAbleToMakeJsonRpcRequest() {
        LOG.info("shouldBeAbleToMakeJsonRpcRequest");
        assertThat(testInterface.testReturnMethod(BigInteger.ONE, Calendar.getInstance()), is("dimanche"));
        assertThat(testInterface.testReturnMethod(BigInteger.ONE, Calendar.getInstance()), is("dimanche"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentxceptionIfThrown() {
        LOG.info("shouldThrowIllegalArgumentxceptionIfThrown");
        testInterface.testReturnMethod(BigInteger.TEN, null);
    }

    @After
    public void shutDownSpark() {
        Spark.stop();
    }

}
