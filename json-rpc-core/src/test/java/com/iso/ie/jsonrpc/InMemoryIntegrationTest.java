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
package com.iso.ie.jsonrpc;


import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

public class InMemoryIntegrationTest {

    private static String ADDRESS = "sum";

    private TestInterface testInterface;

    public interface TestInterface {
        void testMethod(String s, Date d);

        String testReturnMethod(BigInteger b, Calendar c);

        String callCallbackWith(String input);

        void registerCallback(Function<String, String> f);
        
        Future<Integer> testAsync();
    }

    @Before
    public void setUp() {
        final JsonRpcTransport transport = new InMemoryJsonRpcTransport(ADDRESS);

        final JsonRpcFactory jsonRpcFactory = new JsonRpcFactory(transport);

        testInterface = jsonRpcFactory.newClient(TestInterface.class, 10, TimeUnit.DAYS);
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

            @Override
            public Future<Integer> testAsync() {
            	final CompletableFuture<Integer> future = new CompletableFuture<>();
            	
            	future.complete(Integer.valueOf(1));
            	
            	return future;
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
    public void testCallback() {
        testInterface.registerCallback(String::toUpperCase);

        assertEquals("WIBBLE", testInterface.callCallbackWith("wibble"));
    }
    
    @Test
    public void testAsync() throws InterruptedException, ExecutionException {
    	assertEquals(Integer.valueOf(1), testInterface.testAsync().get());
    }
}
