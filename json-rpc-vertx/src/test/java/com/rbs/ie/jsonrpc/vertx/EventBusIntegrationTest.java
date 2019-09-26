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
package com.rbs.ie.jsonrpc.vertx;


import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.rbs.ie.jsonrpc.JsonRpcFactory;
import com.rbs.ie.jsonrpc.JsonRpcTransport;
import com.rbs.ie.jsonrpc.vertx.EventBusJsonRpcTransport;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

@RunWith(VertxUnitRunner.class)
public class EventBusIntegrationTest {

    private static String ADDRESS = "sum";

    private TestInterface testInterface;
    private Vertx vertx;

    public interface TestInterface {
        void testMethod(String s, Date d);

        String testReturnMethod(BigInteger b, Calendar c);

        String callCallbackWith(String input);

        void registerCallback(Function<String, String> f);
    }

    @Before
    public void setUp() {
        vertx = Vertx.vertx();

        final JsonRpcTransport transport = new EventBusJsonRpcTransport(vertx, ADDRESS);

        final JsonRpcFactory jsonRpcFactory = new JsonRpcFactory(transport);

        testInterface = jsonRpcFactory.newClient(TestInterface.class, 10, TimeUnit.SECONDS);
        jsonRpcFactory.registerCallbackType(Function.class);

        jsonRpcFactory.registerService(TestInterface.class, new TestInterface() {
            Function<String, String> function;

            @Override
            public void testMethod(String s, Date d) throws IllegalArgumentException {
                if (s.equals("bad")) {
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
        testInterface.testMethod("", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMethodFailsWithIllegalArgumentException() {
        testInterface.testMethod("bad", null);
    }

    @Test
    public void testReturnMethodSucceeds() {
        assertEquals("vendredi", testInterface.testReturnMethod(BigInteger.valueOf(6), Calendar.getInstance()));
    }

    @Test
    public void testCallback(final TestContext testContext) {
        final Async async = testContext.async();

        testInterface.registerCallback(String::toUpperCase);

        vertx.executeBlocking(future -> {
            future.complete(testInterface.callCallbackWith("wibble"));
        }, result -> {
            assertEquals(true, result.succeeded());
            assertEquals("WIBBLE", result.result());
            async.complete();
        });

        async.awaitSuccess(5000);
    }
}
