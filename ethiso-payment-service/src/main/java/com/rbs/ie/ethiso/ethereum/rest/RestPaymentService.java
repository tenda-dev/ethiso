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
package com.rbs.ie.ethiso.ethereum.rest;

import static com.rbs.ie.ethiso.domain.Party.party;
import static spark.Spark.options;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbs.ie.ethiso.PaymentService;
import com.rbs.ie.ethiso.domain.Bic;
import com.rbs.ie.ethiso.domain.Currency;
import com.rbs.ie.ethiso.domain.Party;
import com.rbs.ie.ethiso.ethereum.contract.NameToAddressRegistrar;
import com.rbs.ie.ethiso.ethereum.rpc.Address;

import spark.Request;
import spark.Route;
import spark.Spark;
import spark.route.HttpMethod;

public class RestPaymentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestPaymentService.class);

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final String REMITTANCE_INFO_UNSTRUCTURED = "RmtInfUstrd";
	
	private static final String ASYNC = "/async";
	private static final String BIC = "/bic";
	private static final String TRUSTLINES_ALLOW = "/trustlines/:bic/:ccy/allow";
	private static final String TRUSTLINES_LIMIT = "/trustlines/:bic/:ccy/limit";
	private static final String TRUSTLINES_BALANCE = "/trustlines/:bic/:ccy/balance";
	private static final String PAYMENTS = "/payments";
	private static final String CCY = "ccy";
	private static final String BIC_PATH = "bic";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";
	private static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
	private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	private static final String ANY = "*";
	private static final String EMPTY_STRING = "";
	private static final int UNPROCESSABLE_ENTITY = 422;

	private final Queue<Future<?>> pendingRequests = new ConcurrentLinkedQueue<>();

	private static final int ONE_SECOND = 1000;

	public void registerRestEndpoints(final int restPort, final PaymentService paymentService, final NameToAddressRegistrar bicRegistrar) {
		LOGGER.info("Setting thread pool to 10 threads");

		Spark.port(restPort);
		Spark.threadPool(500);
		PaymentListenerWebSocket.paymentService = paymentService;  // hideous...
		Spark.webSocket("/paymentsReceived", PaymentListenerWebSocket.class);
		Spark.staticFileLocation("/public");

		patch(BIC, (req, res) -> {
			final Bic bic = OBJECT_MAPPER.readValue(req.body(), RegisterBicBody.class).getBic();

			LOGGER.info("Registering bic [" + bic + "]");

			return paymentService.registerBic(bic);
		});

		patch(TRUSTLINES_ALLOW, (req, res) -> {
			final Bic bic = getBic(req);
			final Currency currency = getCurrency(req);
			final BigInteger allow = getAllow(currency, req);

			final Party party = partyFromABic(bic, bicRegistrar);

			LOGGER.info("Setting allow [" + party + ", " + currency + ", " + allow + "]");

			paymentService.setAllow(party, currency, allow);

			return true;
		});

		patch(ASYNC + TRUSTLINES_ALLOW, (req, res) -> {
			final Bic bic = getBic(req);
			final Currency currency = getCurrency(req);
			final BigInteger allow = getAllow(currency, req);

			final Party party = partyFromABic(bic, bicRegistrar);

			LOGGER.info("Setting allow [" + party + ", " + currency + ", " + allow + "] asynchronously");

			pendingRequests.add(paymentService.setAllowAsync(party, currency, allow));

			return true;
		});

		patch(TRUSTLINES_LIMIT, (req, res) -> {
			final Bic bic = getBic(req);
			final Currency currency = getCurrency(req);
			final BigInteger limit = getLimit(currency, req);

			final Party party = partyFromABic(bic, bicRegistrar);

			LOGGER.info("Setting limit [" + party + ", " + currency + ", " + limit + "]");

			paymentService.setLimit(party, currency, limit);

			return true;
		});

		patch(ASYNC + TRUSTLINES_LIMIT, (req, res) -> {
			final Bic bic = getBic(req);
			final Currency currency = getCurrency(req);
			final BigInteger limit = getLimit(currency, req);

			final Party party = partyFromABic(bic, bicRegistrar);

			LOGGER.info("Setting limit [" + party + ", " + currency + ", " + limit + "] asynchronously");

			pendingRequests.add(paymentService.setLimitAsync(party, currency, limit));

			return true;
		});

		get(TRUSTLINES_BALANCE, (req, res) -> {
			final Bic bic = getBic(req);
			final Party party = partyFromABic(bic, bicRegistrar);

			final Currency currency = getCurrency(req);

			LOGGER.info("Getting balance [" + party + ", " + currency + "]");

			return currency.decimalise(paymentService.getBalance(party, currency));
		});

		patch(PAYMENTS, (req, res) -> {
			final PaymentBody payment = OBJECT_MAPPER.readValue(req.body(), PaymentBody.class);

			LOGGER.info("tk=start,fromBic={},toBic={},fromIban={},toIban={},amt={},ccy={},ref={}", payment.getFromBic(), payment.getToBic(), payment.getFromIban(), payment.getToIban(), payment.getAmount(), payment.getCcy(), payment.getRemittanceInfo());

			final BigInteger scaledAmount = payment.getCcy().deDecimalise(payment.getAmount());
			
			paymentService.sendPayment(payment.getFromBic(), payment.getToBic(), payment.getFromIban(), payment.getToIban(), payment.getCcy(), scaledAmount,
					Collections.singletonMap(REMITTANCE_INFO_UNSTRUCTURED, payment.getRemittanceInfo()));

			return true;
		});

		patch(ASYNC + PAYMENTS, (req, res) -> {
			final PaymentBody payment = OBJECT_MAPPER.readValue(req.body(), PaymentBody.class);

			LOGGER.info("tk=start,fromBic={},toBic={},fromIban={},toIban={},amt={},ccy={},ref={}", payment.getFromBic(), payment.getToBic(), payment.getFromIban(), payment.getToIban(), payment.getAmount(), payment.getCcy(), payment.getRemittanceInfo());

			final BigInteger scaledAmount = payment.getCcy().deDecimalise(payment.getAmount());
			
			pendingRequests.add(paymentService.sendPaymentAsync(payment.getFromBic(), payment.getToBic(), payment.getFromIban(), payment.getToIban(), payment.getCcy(), scaledAmount,
					Collections.singletonMap(REMITTANCE_INFO_UNSTRUCTURED, payment.getRemittanceInfo())));

			return true;
		});
		
		final Timer timer = new Timer(true);
		
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				final Iterator<Future<?>> futures = pendingRequests.iterator();
				
				while (futures.hasNext()) {
					final Future<?> future = futures.next();
					
					if (future.isDone()) {
						try {
							LOGGER.info("Completed async request with " + future.get());
						} catch (final InterruptedException | ExecutionException e) {
							LOGGER.warn("Failed async request with " + e);
						}
						
						futures.remove();
					}
				}
			}
		}, ONE_SECOND, ONE_SECOND);
	}

	private Party partyFromABic(Bic bic, NameToAddressRegistrar bicRegistrar) {
		Address address = bicRegistrar.getAddress(bic.toString());
		return party(address.toString());
	}

	private static void get(final String path, final Route route) {
		allowCors(path, HttpMethod.get);
		Spark.get(path, wrap(route));
	}

	private static void patch(final String path, final Route route) {
		allowCors(path, HttpMethod.patch);
		Spark.patch(path, wrap(route));
	}

	private static void allowCors(final String path, final HttpMethod method) {
		options(path, (req, res) -> {

			res.header(ACCESS_CONTROL_ALLOW_ORIGIN, ANY);
			res.header(ACCESS_CONTROL_ALLOW_METHODS, method.name().toUpperCase());
			res.header(ACCESS_CONTROL_ALLOW_HEADERS, req.headers(ACCESS_CONTROL_REQUEST_HEADERS));

			return EMPTY_STRING;
		});
	}

	private static Route wrap(final Route route) {
		return (req, res) -> {
			Object result;

			res.header(CONTENT_TYPE, APPLICATION_JSON);
			res.header(ACCESS_CONTROL_ALLOW_ORIGIN, ANY);

			try {
				result = route.handle(req, res);
			} catch (Exception e) {
				LOGGER.warn(e.toString());
				result = OBJECT_MAPPER.writeValueAsString(e.getMessage());
				res.status(UNPROCESSABLE_ENTITY);
			}

			return result;
		};
	}

	private static Bic getBic(final Request request) {
		try {
			return Bic.bic(request.params(BIC_PATH));
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException("Bic [" + request.params(BIC) + "] is invalid");
		}
	}

	private static Currency getCurrency(final Request request) {
		try {
			return Currency.valueOf(request.params(CCY).toUpperCase());
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException("Currency [" + request.params(CCY) + "] is invalid");
		}
	}

	private static BigInteger getAllow(final Currency ccy, final Request request) {
		try {
			return ccy.deDecimalise(OBJECT_MAPPER.readValue(request.body(), SetAllowBody.class).getAllow());
		} catch (final IOException e) {
			throw new IllegalArgumentException("Amount [" + request.body() + "] is invalid");
		}
	}
	
	private static BigInteger getLimit(final Currency ccy, final Request request) {
		try {
			return ccy.deDecimalise(OBJECT_MAPPER.readValue(request.body(), SetLimitBody.class).getLimit());
		} catch (final IOException e) {
			throw new IllegalArgumentException("Amount [" + request.body() + "] is invalid");
		}
	}
}