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
package com.rbs.ie.jsonrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractJsonRpcTransport implements JsonRpcTransport {

	private static final String SLASH = "/";
	
	private final String key;
	private final AtomicInteger callbackId = new AtomicInteger();
	private final List<JsonRpcTransport> derivedTransports = new ArrayList<>();
	private volatile boolean closed = false;
	
	public AbstractJsonRpcTransport(final String key) {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		this.key = key;
	}
	
	protected String getKey() {
		return key;
	}
	
	@Override
	public void send(final String request, final Consumer<String> resultHandler, final Consumer<Throwable> errorHandler) {
		if (!closed) {
			sendInternal(request, resultHandler, errorHandler);
		} else {
			throw new IllegalStateException();
		}
	}
	
	protected abstract void sendInternal(String request, Consumer<String> resultHandler, Consumer<Throwable> errorHandler);

	@Override
	public void consumer(final BiConsumer<String, Consumer<String>> handler) {
		if (!closed) {
			consumerInternal(handler);
		} else {
			throw new IllegalStateException();
		}		
	}
	
	protected abstract void consumerInternal(BiConsumer<String, Consumer<String>> handler);
	
	@Override
	public JsonRpcTransport getCallbackTransport() {
		return deriveCallbackTransport(key + SLASH + callbackId.getAndIncrement());
	}

	@Override
	public JsonRpcTransport deriveCallbackTransport(final String callbackAddress) {
		final JsonRpcTransport derivedTransport = deriveCallbackTransportInternal(callbackAddress);
		
		derivedTransports.add(derivedTransport);
		
		return derivedTransport;
	}
	
	protected abstract JsonRpcTransport deriveCallbackTransportInternal(String callbackAddress);

	@Override
	public String getSerialisedForm() {
		return key;
	}

	@Override
	public void close() {
		closed = true;
		
		for (final JsonRpcTransport derivedTranport : derivedTransports) {
			derivedTranport.close();
		}
		
		closeInternal();
	}
	
	protected abstract void closeInternal();
}
