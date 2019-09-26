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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.rbs.ie.jsonrpc.AbstractJsonRpcTransport;
import com.rbs.ie.jsonrpc.JsonRpcTransport;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;

public class EventBusJsonRpcTransport extends AbstractJsonRpcTransport {

    private final Vertx vertx;
    private final List<MessageConsumer<?>> consumers = new ArrayList<>();
    
    public EventBusJsonRpcTransport(final Vertx vertx, final String address) {
        super(address);
    	
        this.vertx = vertx;
    }

    @Override
    protected void sendInternal(final String request, final Consumer<String> resultHandler, final Consumer<Throwable> errorHandler) {
        vertx.eventBus().send(getKey(), request, reply -> {
            if (reply.succeeded()) {
                resultHandler.accept(String.valueOf(reply.result().body()));
            } else {
                errorHandler.accept(reply.cause());
            }
        });
    }

    @Override
    protected void consumerInternal(final BiConsumer<String, Consumer<String>> handler) {
        consumers.add(vertx.eventBus().consumer(getKey(), message -> {
            vertx.executeBlocking(future -> {
            	handler.accept(String.valueOf(message.body()), result -> {
            		future.complete(result);
            	});
            }, result -> {
                message.reply(result.result());
            });
        }));
    }
    
    @Override
    protected void closeInternal() {
    	for (final MessageConsumer<?> consumer : consumers) {
    		consumer.unregister();
    	}
    }

	@Override
	protected JsonRpcTransport deriveCallbackTransportInternal(final String callbackAddress) {
		return new EventBusJsonRpcTransport(vertx, callbackAddress);
	}
}