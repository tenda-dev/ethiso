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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class InMemoryJsonRpcTransport extends AbstractJsonRpcTransport {

	private final BlockingQueue<Task> waitingRequests = new LinkedBlockingQueue<>();
	private Thread thread;
	private final ConcurrentMap<String, InMemoryJsonRpcTransport> transports = new ConcurrentHashMap<>();

	public InMemoryJsonRpcTransport(final String key) {
		super(key);
	}

	@Override
	protected void sendInternal(final String request, final Consumer<String> resultHandler,
			final Consumer<Throwable> errorHandler) {
		waitingRequests.add(new Task(request, resultHandler, errorHandler));
	}

	@Override
	protected void consumerInternal(final BiConsumer<String, Consumer<String>> handler) {
		if (handler != null) {
			if (thread != null) {
				thread.interrupt();
			}

			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						while (true) {
							waitingRequests.take().execute(handler);
						}
					} catch (final InterruptedException e) {
						// Shutting down
					}
				}
			});
			
			thread.start();
		}
	}

	@Override
	protected JsonRpcTransport deriveCallbackTransportInternal(final String callbackAddress) {
		if (!transports.containsKey(callbackAddress)) {
			transports.putIfAbsent(callbackAddress, new InMemoryJsonRpcTransport(callbackAddress));
		}

		return transports.get(callbackAddress);
	}

	@Override
	protected void closeInternal() {
		if (thread != null) {
			thread.interrupt();

			try {
				thread.join();
			} catch (final InterruptedException e) {
				// Shutting down
			}
		}
	}

	private class Task {
		private final String request;
		private final Consumer<String> resultHandler;
		private final Consumer<Throwable> errorHandler;

		public Task(final String request, final Consumer<String> resultHandler,
				final Consumer<Throwable> errorHandler) {
			this.request = request;
			this.resultHandler = resultHandler;
			this.errorHandler = errorHandler;
		}

		public void execute(final BiConsumer<String, Consumer<String>> handler) throws InterruptedException {
			try {
				handler.accept(request, resultHandler);
			} catch (final RuntimeException e) {
				errorHandler.accept(e);
			}
		}
	}
}