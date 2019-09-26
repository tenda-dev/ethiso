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
package com.iso.ie.ethiso.ethereum.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.iso.ie.ethiso.domain.Bic;
import com.iso.ie.ethiso.ethereum.ContractProxy;
import com.iso.ie.ethiso.ethereum.Ethereum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iso.ie.ethiso.PaymentListener;
import com.iso.ie.ethiso.PaymentService;
import com.iso.ie.ethiso.TrustlineListener;
import com.iso.ie.ethiso.domain.Currency;
import com.iso.ie.ethiso.domain.Iban;
import com.iso.ie.ethiso.domain.Party;
import com.iso.ie.ethiso.ethereum.rpc.Address;

import static com.iso.ie.ethiso.domain.Bic.bic;
import static com.iso.ie.ethiso.domain.Iban.iban;

public class PaymentServiceImpl extends ContractProxy implements PaymentService {

    private static final Map<String, Object> EMPTY_MAP = Collections.emptyMap();
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String FROM_BIC = "fromBic";
    private static final String TO_BIC = "toBic";
    private static final String FROM_IBAN = "fromIban";
    private static final String TO_IBAN = "toIban";
    private final ConcurrentMap<String, CompletableFuture<Void>> pendingPayments = new ConcurrentHashMap<>();
	private final NameToAddressRegistrar bicRegistrar;
	private final Address fromAccount;

	public PaymentServiceImpl(final Ethereum ethereum, final Mode mode, final Address contractAddress, NameToAddressRegistrar bicRegistrar) {
		super(ethereum, mode, contractAddress, "PaymentService");
		this.bicRegistrar = bicRegistrar;

		addEventListener("ResultMessage", (x) -> {
			final String messageId = x[1].toString();
			final String message = x[2].toString();
			final int errorType = ((BigInteger) x[3]).intValueExact();
			
			final CompletableFuture<Void> handler = pendingPayments.remove(messageId);
			
			if (handler != null) {
				if (errorType != 0) {
					LOGGER.error("Payment failed " + Arrays.toString(x));
					
					handler.completeExceptionally(new IllegalStateException(message));
				} else {
					handler.complete(null);
				}
			} else {
				LOGGER.warn("No completion handler for messageId [" + messageId + "]");
			}	
		}, ethereum.getFromAccount().toWideAddressString());

		fromAccount = ethereum.getFromAccount();
	}

	@Override
	public boolean registerBic(Bic bic) {
        bicRegistrar.setAddress(bic.toString(), fromAccount);
        return bicRegistrar.getAddress(bic.toString()).equals(fromAccount);
    }

	@Override
	public void setAllow(final Party them, final Currency currency, final BigInteger allowedAmount) {
		try {
			setAllowAsync(them, currency, allowedAmount).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Future<Void> setAllowAsync(final Party them, final Currency currency, final BigInteger allowedAmount) {
		LOGGER.debug("setAllowAsync [them=" + them + ", ccy=" + currency + ", allowedAmount=" + allowedAmount + "]");

		return sendTransaction("setAllow", new Address(them.toString()), currency.toString(), allowedAmount)
				.thenRun(() -> {
					LOGGER.debug("setAllowAsync returned");
				}).toCompletableFuture();
	}

	@Override
	public void setLimit(final Party them, final Currency currency, final BigInteger limitAmount) {
		try {
			setLimitAsync(them, currency, limitAmount).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Future<Void> setLimitAsync(final Party them, Currency currency, final BigInteger limitAmount) {
		LOGGER.debug("setLimitAsync [them=" + them + ", ccy=" + currency + ", limitAmount=" + limitAmount + "]");

		return sendTransaction("setLimit", new Address(them.toString()), currency.toString(), limitAmount)
				.thenRun(() -> {
					LOGGER.debug("setLimitAsync returned");
				}).toCompletableFuture();
	}

	@Override
	public void sendPayment(final Bic fromBic, final Bic toBic, final Iban fromIban, final Iban toIban, final Currency currency, final BigInteger paymentAmount,
			final Map<String, Object> additionalProperties) {
		try {
			sendPaymentAsync(fromBic, toBic, fromIban, toIban, currency, paymentAmount, additionalProperties).get();
		} catch (InterruptedException | ExecutionException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			}
			
			throw new RuntimeException(e.getCause());
		}
	}

	@Override
	public Future<Void> sendPaymentAsync(final Bic fromBic, final Bic toBic, final Iban fromIban, final Iban toIban, final Currency currency,
			final BigInteger paymentAmount, final Map<String, Object> additionalPropertiesOption) {
		String additionalPropertiesString = "{}";

        Map<String, Object> additionalProperties = additionalPropertiesOption != null ? new HashMap<>(additionalPropertiesOption) : new HashMap<>();
        // hack for v1.1
        additionalProperties.put(FROM_BIC, fromBic.toString());
        additionalProperties.put(TO_BIC, toBic.toString());
        additionalProperties.put(FROM_IBAN, fromIban.toString());
        additionalProperties.put(TO_IBAN, toIban.toString());

		try {
			additionalPropertiesString = OBJECT_MAPPER.writeValueAsString(additionalProperties);
		} catch (JsonProcessingException e) {
			LOGGER.warn("Failed to send additionalPropeties " + additionalProperties);
		}

        Address toAddress = bicRegistrar.getAddress(toBic.toString());

        LOGGER.debug("sendPaymentAsync [toAddress="+toAddress+", fromBic=" + fromBic + ", toBic=" + toBic +", fromIban=" + fromIban + ", toIban=" + toIban + ", ccy="
                + currency + ", paymentAmount=" + paymentAmount + ", additionalProperties=" + additionalPropertiesString + "]");

		final String messageId = UUID.randomUUID().toString();
		
		final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
		
		pendingPayments.put(messageId, completableFuture);
		
		sendTransaction("sendPayment", toAddress, currency.toString(), paymentAmount,
				messageId, additionalPropertiesString);
		
		return completableFuture;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerPaymentListener(final PaymentListener paymentListener) {
		addEventListener("PaymentMessage", (x) -> {

			final Currency ccy = Currency.valueOf(x[4].toString());
			final BigInteger amount = (BigInteger) x[3];
			Map<String, Object> additionalProperties = EMPTY_MAP;

			try {
				additionalProperties = new ObjectMapper().readValue(x[5].toString(), Map.class);
			} catch (Exception e) {
				LOGGER.warn("Failed to deserialise additional properties " + e);
			}

            final Bic fromBic = bic((String) additionalProperties.get(FROM_BIC));
            final Bic toBic = bic((String) additionalProperties.get(TO_BIC));
            final Iban fromIban = iban((String) additionalProperties.get(FROM_IBAN));
            final Iban toIban = iban((String) additionalProperties.get(TO_IBAN));
            
            // We are only interested in Payments that are intended for us, ie toBic is equal
            // to a BIC that has been registered for this bank.
            Address toAddr = this.bicRegistrar.getAddress(((String)additionalProperties.get(TO_BIC)));
            
            //                                         1234567890123456789012345678901234567890123456789012345678901234
            if (toAddr.toWideAddressString().equals("0x0000000000000000000000000000000000000000000000000000000000000000")) {
            	LOGGER.warn("BIC {} resolved to Address {}", TO_BIC, toAddr);
            }            	
            
            if (toAddr.toWideAddressString().equals(fromAccount.toWideAddressString())) {
            	paymentListener.paymentReceived(fromBic, toBic, fromIban, toIban, ccy, amount, additionalProperties);
            }            
		});
	}

	@Override
	public void registerTrustlineListener(final TrustlineListener trustlineListener) {
	
		LOGGER.info("In RegisterTrustlineListener");
		
		addEventListener("TrustlineModified", (x) -> {
		
			LOGGER.info("Received TrustlineModified " + Arrays.toString(x));
			
			LOGGER.info("class name is whatever :"+ x[1].getClass().getName());
			
			final Address them = (Address) x[1];  

			
			final Currency currency =Currency.valueOf(x[2].toString());
			final BigInteger forwardLimit = (BigInteger) x[4];
			final BigInteger reverseLimit = (BigInteger) x[5];
			final BigInteger forwardAllow = (BigInteger) x[6];
			final BigInteger reverseAllow = (BigInteger) x[7];
			
			trustlineListener.trustlineModified(Party.party(them.toString()), currency, forwardLimit, reverseLimit, forwardAllow, reverseAllow);
		});
	}

	@Override
	public BigInteger getBalance(final Party them, final Currency currency) {
		final Object[] result = call("getAmountOwedTo", new Address(them.toString()), currency.toString());

		if (result.length != 1 || !(result[0] instanceof BigInteger)) {
			throw new IllegalArgumentException("Invalid return type [" + result[0].getClass());
		}

		return (BigInteger) result[0];
	}
}
