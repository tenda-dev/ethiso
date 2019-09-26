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
package com.std.ie.jsonrpc.rabbitmq;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.std.ie.jsonrpc.AbstractJsonRpcTransport;
import com.std.ie.jsonrpc.JsonRpcTransport;

public class RabbitMQJsonRpcTransport extends AbstractJsonRpcTransport {

	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQJsonRpcTransport.class);

	private static final String OUT_SUFFIX = "/out";

	private static final String IN_SUFFIX = "/in";

	private static final String EXCHANGE = "";

	private final ConnectionFactory connectionFactory;
	private final Connection connection;
	private final Channel channel;
	private final ConcurrentMap<String, Consumer<String>> resultHandlers = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, Consumer<Throwable>> errorHandlers = new ConcurrentHashMap<>();

	private AtomicInteger correlationIdCounter = new AtomicInteger();

	private boolean listening = false;

	public RabbitMQJsonRpcTransport(final ConnectionFactory connectionFactory, final String routingKey) {
		super(routingKey);

		this.connectionFactory = connectionFactory;

		try {
			this.connection = getConnection(connectionFactory);

			this.channel = connection.createChannel();
		} catch (final IOException | InterruptedException | TimeoutException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void sendInternal(final String request, final Consumer<String> resultHandler,
			final Consumer<Throwable> errorHandler) {
		if (!listening) {
			try {
				channel.queueDeclare(getKey() + IN_SUFFIX, false, false, true, null);
				channel.queueDeclare(getKey() + OUT_SUFFIX, false, false, true, null);

				channel.basicConsume(getKey() + IN_SUFFIX, true, new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(final String consumerTag, final Envelope envelope,
							final BasicProperties properties, final byte[] body) throws IOException {

						final String correlationId = properties.getCorrelationId();

						if (resultHandlers.containsKey(correlationId)) {
							final Consumer<String> resultHandler = resultHandlers.remove(correlationId);

							// Not sure whether we can actually use this?
							resultHandlers.remove(correlationId);

							resultHandler.accept(new String(body));
						} else {
							throw new IllegalStateException(
									"No handler for " + correlationId + " " + resultHandlers + " on queue " + getKey());
						}
					}
				});

				listening = true;
			} catch (final IOException e) {
				errorHandler.accept(e);
			}
		}

		final String correlationId = String.valueOf(correlationIdCounter.getAndIncrement());

		if (listening) {
			resultHandlers.put(correlationId, resultHandler);
			errorHandlers.put(correlationId, errorHandler);

			try {
				channel.basicPublish(EXCHANGE, getKey() + OUT_SUFFIX,
						new BasicProperties.Builder().correlationId(correlationId).build(), request.getBytes());
			} catch (final IOException e) {
				resultHandlers.remove(correlationId);
				errorHandlers.remove(correlationId);

				throw new IllegalStateException(e);
			}
		}
	}

	@Override
	protected void consumerInternal(final BiConsumer<String, Consumer<String>> handler) {
		try {
			channel.queueDeclare(getKey() + OUT_SUFFIX, false, false, true, null);

			channel.basicConsume(getKey() + OUT_SUFFIX, true, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(final String consumerTag, final Envelope envelope,
						final BasicProperties properties, final byte[] body) throws IOException {
					handler.accept(new String(body), result -> {
						try {
							channel.basicPublish(EXCHANGE, getKey() + IN_SUFFIX,
									new BasicProperties.Builder().correlationId(properties.getCorrelationId()).build(),
									result.getBytes());
						} catch (final IOException e) {
							throw new RuntimeException(e);
						}
					});
				}
			});
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void closeInternal() {
		try {
			channel.close();
		} catch (final IOException | TimeoutException e) {
			LOGGER.warn("Failed to close channel [" + e + "]");
		}

		try {
			connection.close();
		} catch (final IOException e) {
			LOGGER.warn("Failed to close channel [" + e + "]");
		}
	}

	@Override
	protected JsonRpcTransport deriveCallbackTransportInternal(final String callbackAddress) {
		return new RabbitMQJsonRpcTransport(connectionFactory, callbackAddress);
	}

	private static Connection getConnection(final ConnectionFactory connectionFactory)
			throws InterruptedException, TimeoutException {
		Connection connection = null;

		final long start = System.currentTimeMillis();

		while (connection == null) {
			try {
				connection = connectionFactory.newConnection();
			} catch (IOException | TimeoutException e) {
				// Do nothing
			}

			if (connection == null) {
				final long seconds = (System.currentTimeMillis() - start) / 1000;

				if (seconds < 30) {
					LOGGER.info("Waiting for rabbit (" + seconds + " seconds)");

					Thread.sleep(1000);
				} else {
					throw new TimeoutException("Gave up waiting for rabbit after " + 30 + " seconds");
				}
			}
		}

		return connection;
	}
}