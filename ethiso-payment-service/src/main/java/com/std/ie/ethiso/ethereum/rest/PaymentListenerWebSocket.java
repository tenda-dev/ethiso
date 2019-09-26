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
package com.std.ie.ethiso.ethereum.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.std.ie.ethiso.PaymentListener;
import com.std.ie.ethiso.PaymentService;
import com.std.ie.ethiso.domain.Bic;
import com.std.ie.ethiso.domain.Currency;
import com.std.ie.ethiso.domain.Iban;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class PaymentListenerWebSocket implements PaymentListener {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentListenerWebSocket.class);
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    public static PaymentService paymentService;

    public PaymentListenerWebSocket() {
        paymentService.registerPaymentListener(this);
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        LOGGER.info("someone connnected to the web socket");
        sessions.add(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        LOGGER.info("someone disconnected from the web socket "+reason);
        sessions.remove(session);
    }

    @Override
    public void paymentReceived(Bic fromBic, Bic toBic, Iban fromIban, Iban toIban, Currency currency, BigInteger paymentAmount, Map<String, Object> additionalProperties) {
        try {
            String message = String.format("{\"tk\":\"end\",\"fromBic\":\"%s\",\"toBic\":\"%s\",\"fromIban\":\"%s\",\"toIban\":\"%s\",\"amt\":\"%d\",\"ccy\":\"%s\",\"ref\":%s}", fromBic, toBic, fromIban, toIban, paymentAmount.intValue(), currency, OBJECT_MAPPER.writeValueAsString(additionalProperties));
            for (Session session : sessions) {
                session.getRemote().sendString(message);
            }
        } catch (IOException e) {
            LOGGER.error("issue sending message",e);
        }
    }
}
