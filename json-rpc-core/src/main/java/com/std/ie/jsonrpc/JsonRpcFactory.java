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
package com.std.ie.jsonrpc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonRpcFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonRpcFactory.class);

	private static ClassLoader CLASSLOADER = JsonRpcFactory.class.getClassLoader();

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final CompletionService<Void> completionService = new ExecutorCompletionService<>(
			Executors.newFixedThreadPool(5));
	private final JsonRpcTransport transport;
	private final AtomicInteger id = new AtomicInteger();

	public JsonRpcFactory(final JsonRpcTransport transport) {
		this.transport = transport;
	}

	public <T> T newClient(final Class<T> iface, final long timeout, final TimeUnit timeoutUnits) {
		return newClient(transport, iface, timeout, timeoutUnits);
	}

	public <T> void registerService(final Class<T> iface, final T instance) {
		registerService(transport, iface, instance);
	}

	public <T> void registerCallbackType(Class<T> functionClass) {

		SimpleModule module = new SimpleModule();
		module.addSerializer(functionClass, new CallbackSerializer<>(functionClass));
		module.addDeserializer(functionClass, new CallbackDeserializer<>(functionClass));

		OBJECT_MAPPER.registerModule(module);
	}

	private <T> T newClient(final JsonRpcTransport transport, final Class<T> iface, final long timeout,
			final TimeUnit timeoutUnits) {
		return iface.cast(Proxy.newProxyInstance(CLASSLOADER, new Class<?>[] { iface }, (proxy, method, args) -> {
			Object ret;

			if (!method.getDeclaringClass().equals(Object.class)) {
				CompletableFuture<Object> completableFuture = new CompletableFuture<>();

				JsonRpcRequest request = new JsonRpcRequest(id.getAndIncrement(), method.getName(), args);
				LOGGER.debug(request.toString());
				transport.send(OBJECT_MAPPER.writeValueAsString(
						request), result -> {
							try {
								Class<?> returnType = method.getReturnType();

								if (Future.class.isAssignableFrom(returnType)) {
									returnType = (Class<?>) ((ParameterizedType) method.getGenericReturnType())
											.getActualTypeArguments()[0];
								}

								completableFuture.complete(OBJECT_MAPPER.readValue(result, JsonRpcResponse.class)
										.getResultAs(returnType, OBJECT_MAPPER));
							} catch (Throwable t) {
								completableFuture.completeExceptionally(t);
							}
						}, error -> {
							completableFuture.completeExceptionally(error);
						});

				if (Future.class.isAssignableFrom(method.getReturnType())) {
					ret = completableFuture;
				} else {
					try {
						ret = completableFuture.get(timeout, timeoutUnits);
					} catch (final ExecutionException e) {
						if (e.getCause() instanceof JsonRpcException) {
							throw (JsonRpcException) e.getCause();
						}
						throw e.getCause();
					} catch (final TimeoutException e) {
						throw new IllegalStateException("Time out waiting for response");
					}
				}
			} else {
				ret = method.invoke(proxy, args);
			}

			return ret;
		}));
	}

	private <T> void registerService(final JsonRpcTransport transport, final Class<T> iface, final T instance) {
		transport.consumer((message, responseHandler) -> {
			Throwable t = null;
			int id = -1;

			boolean matched = false;

			Method actualMethod = null;
			Object[] params = null;

			try {
				final JsonRpcRequest request = OBJECT_MAPPER.readValue(message, JsonRpcRequest.class);

				id = Integer.valueOf(request.getId());

				for (final Method method : iface.getMethods()) {
					if (!matched && request.getMethod().equals(method.getName())
							&& method.getParameterCount() == request.getParams().size()) {
						try {
							params = convertParams(method, request.getParams());

							matched = true;

							actualMethod = method;
						} catch (IllegalArgumentException e) {
							// Couldn't convert params, try next method
						}
					}
				}
			} catch (final IOException e) {
				t = e;
			} catch (final UndeclaredThrowableException e) {
				t = e.getUndeclaredThrowable();
			}

			if (!matched) {
				t = new NoSuchMethodError(String.valueOf(message));
			}

			try {
				submit(instance, actualMethod, params, t, id, responseHandler);
			} catch (final Exception e) {
				throw new IllegalStateException(e);
			}
		});
	}

	private void submit(final Object instance, final Method method, final Object[] params, final Throwable t,
			final int id, final Consumer<String> responseHandler) {
		completionService.submit(() -> {
			Object result = null;
			Throwable innerT = null;

			try {
				result = method.invoke(instance, params);
			} catch (final InvocationTargetException e) {
				innerT = e.getTargetException();
			}

			final Throwable finalT = innerT;

			if (result != null && Future.class.isAssignableFrom(result.getClass())) {
				((CompletionStage<?>) result).handle((r, e) -> {
					reply(id, responseHandler, r, t != null ? t : finalT != null? finalT : e);

					return null;
				});
			} else {
				reply(id, responseHandler, result, t != null ? t : innerT);
			}

			return null;
		});
	}

	private static void reply(final int id, final Consumer<String> responseHandler, final Object value,
			final Throwable t) {
		try {
			responseHandler.accept(OBJECT_MAPPER.writeValueAsString(new JsonRpcResponse(id, value, t)));
		} catch (final JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private Object[] convertParams(final Method method, final List<Object> params) {
		final Object[] array = new Object[params != null ? params.size() : 0];
		int pIdx = 0;

		while (pIdx < array.length) {
			array[pIdx] = OBJECT_MAPPER.convertValue(params.get(pIdx), method.getParameterTypes()[pIdx]);

			pIdx++;
		}

		return array;
	}

	private class CallbackSerializer<T> extends JsonSerializer<T> {

		private Class<T> cls;

		public CallbackSerializer(Class<T> cls) {
			this.cls = cls;
		}

		@Override
		public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
				throws IOException, JsonProcessingException {
			final JsonRpcTransport callbackTransport = transport.getCallbackTransport();

			jsonGenerator.writeString(callbackTransport.getSerialisedForm());

			registerService(callbackTransport, cls, t);
		}

	}

	private class CallbackDeserializer<T> extends JsonDeserializer<T> {

		private Class<T> cls;

		public CallbackDeserializer(Class<T> cls) {
			this.cls = cls;
		}

		@Override
		public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
				throws IOException, JsonProcessingException {
			String transportSerialisedForm = jsonParser.getValueAsString();

			return newClient(transport.deriveCallbackTransport(transportSerialisedForm), cls, 10000,
					TimeUnit.MILLISECONDS);
		}
	}
}