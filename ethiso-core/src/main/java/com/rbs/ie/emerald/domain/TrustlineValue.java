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
package com.rbs.ie.emerald.domain;

import java.math.BigInteger;

public class TrustlineValue {

    TrustlineView view;

    public TrustlineValue() {
        this(new DefaultTrustlineView());
    }

    private TrustlineValue(final TrustlineView view) {
        this.view = view;
    }

    public void setLimit(final BigInteger limitAmount) {
        view.getP1ToP2().setLimit(limitAmount);
    }

    public BigInteger getLimit() {
        return view.getP1ToP2().getLimit();
    }

    public void setAllow(final BigInteger allowAmount) {
        view.getP2ToP1().setAllow(allowAmount);
    }

    public BigInteger getAllow() {
        return view.getP2ToP1().getAllow();
    }

    public void sendPayment(final BigInteger paymentAmount) {
        if (paymentAmount.signum() == -1) {
            throw new IllegalArgumentException("paymentAmount < 0");
        }

        BigInteger newBalance = view.getP2OwesP1().add(paymentAmount);

        if (newBalance.compareTo(view.getP1ToP2().getLimit().min(view.getP1ToP2().getAllow())) > 0) {
            throw new IllegalArgumentException(view.getP2OwesP1() + " + " + paymentAmount + " > min(" + view.getP1ToP2().getLimit() + ", " + view.getP1ToP2().getAllow() + ")");
        }

        view.setP2OwesP1(newBalance);
    }

    public BigInteger getBalance() {
        return view.getP2OwesP1();
    }

    public TrustlineValue reverse() {
        return new TrustlineValue(new ReverseTrustlineView(view));
    }

    interface TrustlineView {
        AllowAndLimit getP1ToP2();

        AllowAndLimit getP2ToP1();

        BigInteger getP2OwesP1();

        void setP2OwesP1(BigInteger p2OwesP1);
    }

    private static class DefaultTrustlineView implements TrustlineView {
        // Negative implies p1 owes p2, using BigInteger - this implies a minimum sub-divisibility per ccy e.g. GBP = pence
        private BigInteger p2OwesP1 = BigInteger.ZERO;
        private AllowAndLimit p1ToP2 = new AllowAndLimit();
        private AllowAndLimit p2ToP1 = new AllowAndLimit();

        public AllowAndLimit getP1ToP2() {
            return p1ToP2;
        }

        public AllowAndLimit getP2ToP1() {
            return p2ToP1;
        }

        public BigInteger getP2OwesP1() {
            return p2OwesP1;
        }

        public void setP2OwesP1(BigInteger balance) {
            p2OwesP1 = balance;
        }
    }

    private static class ReverseTrustlineView implements TrustlineView {

        private TrustlineView delegate;

        ReverseTrustlineView(final TrustlineView delegate) {
            this.delegate = delegate;
        }

        public AllowAndLimit getP1ToP2() {
            return delegate.getP2ToP1();
        }

        public AllowAndLimit getP2ToP1() {
            return delegate.getP1ToP2();
        }

        public BigInteger getP2OwesP1() {
            return delegate.getP2OwesP1().negate();
        }

        public void setP2OwesP1(BigInteger balance) {
            delegate.setP2OwesP1(balance.negate());
        }
    }

    private static class AllowAndLimit {
        private BigInteger allow = BigInteger.ZERO;
        private BigInteger limit = BigInteger.ZERO;

        void setAllow(final BigInteger allowAmount) {
            if (allowAmount.signum() == -1) {
                throw new IllegalArgumentException("allowAmount < 0");
            }
            allow = allowAmount;
        }

        void setLimit(final BigInteger limitAmount) {
            if (limitAmount.signum() == -1) {
                throw new IllegalArgumentException("limitAmount < 0");
            }
            limit = limitAmount;
        }

        BigInteger getAllow() {
            return allow;
        }

        BigInteger getLimit() {
            return limit;
        }
    }
}