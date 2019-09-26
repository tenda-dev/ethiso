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

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.rbs.ie.jsonrpc.AbstractJsonRpcTransport;
import com.rbs.ie.jsonrpc.JsonRpcTransport;

import spark.Service;

public class HttpJsonRpcTransport extends AbstractJsonRpcTransport {

	private static final int STATUS_SUCCESS = 200;
	private static final String CONTENT_TYPE = "Content-type";
	private static final String ACCEPT = "Accept";
	private static final String APPLICATION_JSON = "application/json";
	private static final String EMPTY_PATH = "/";

	private static final Map<Integer, Service> sparkServices = new HashMap<>();
	private static final Map<Service, AtomicInteger> serviceReferenceCount = new HashMap<>();

	private final InetSocketAddress socketAddress;
	private final Service service;

	private final CloseableHttpClient httpClient;

	public HttpJsonRpcTransport(final InetSocketAddress socketAddress) {
		this(socketAddress, EMPTY_PATH);
	}

	public HttpJsonRpcTransport(final InetSocketAddress socketAddress, final String path) {
		super(path);

		this.socketAddress = socketAddress;
		this.service = getSharedService();

		final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
		poolingHttpClientConnectionManager.setMaxTotal(100);
		poolingHttpClientConnectionManager.setDefaultMaxPerRoute(50);

		httpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
	}

	Service getService() {
		return service;
	}

	@Override
	protected void sendInternal(final String request, final Consumer<String> resultHandler,
			final Consumer<Throwable> errorHandler) {
		final HttpHost host = new HttpHost(socketAddress.getHostString(), socketAddress.getPort());

		final HttpPost httpPost = new HttpPost(getKey());

		httpPost.setEntity(new StringEntity(request, Charsets.UTF_8));
		httpPost.setHeader(ACCEPT, APPLICATION_JSON);
		httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);

		try (CloseableHttpResponse response = httpClient.execute(host, httpPost)) {
			final StatusLine statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == STATUS_SUCCESS) {
				HttpEntity entity = response.getEntity();
				String responseText = EntityUtils.toString(entity);
				resultHandler.accept(responseText);

				EntityUtils.consume(entity);
			} else {
				errorHandler.accept(new IllegalStateException(statusLine.toString()));
			}
		} catch (RuntimeException re) {
			errorHandler.accept(re);
		} catch (Exception e) {
			errorHandler.accept(new IllegalStateException(e));
		}
	}

	@Override
	protected void consumerInternal(final BiConsumer<String, Consumer<String>> handler) {
		service.init();

		service.awaitInitialization();

		service.post(getKey(), (request, response) -> {
			final CompletableFuture<String> reply = new CompletableFuture<>();
			handler.accept(request.body(), result -> {
				response.type(APPLICATION_JSON);
				response.status(STATUS_SUCCESS);

				reply.complete(result);
			});

			return reply.get();
		});
	}

	@Override
	protected JsonRpcTransport deriveCallbackTransportInternal(String callbackAddress) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void closeInternal() {
		ungetSharedService();
	}

	private Service getSharedService() {
		final Integer port = Integer.valueOf(socketAddress.getPort());

		synchronized (HttpJsonRpcTransport.class) {
			Service service = sparkServices.get(port);

			if (service == null) {
				service = Service.ignite();

				service.port(port.intValue());

				sparkServices.put(port, service);
				serviceReferenceCount.put(service, new AtomicInteger(1));
			} else {
				serviceReferenceCount.get(service).incrementAndGet();
			}

			return service;
		}
	}

	private void ungetSharedService() {
		final Integer port = Integer.valueOf(socketAddress.getPort());

		synchronized (HttpJsonRpcTransport.class) {
			final AtomicInteger referenceCount = serviceReferenceCount.get(service);

			if (referenceCount.decrementAndGet() == 0) {
				sparkServices.remove(port);

				service.stop();

				serviceReferenceCount.remove(service);
			}
		}
	}
}