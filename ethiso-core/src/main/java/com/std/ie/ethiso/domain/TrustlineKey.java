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
package com.std.ie.ethiso.domain;

public class TrustlineKey {

    private final Party p1;
    private final Party p2;
    private final Currency ccy;

    public TrustlineKey(Party p1, Party p2, Currency ccy) {
        if (p1 == null || p2 == null || ccy == null || p1.equals(p2)) {
            throw new IllegalArgumentException("invalid args [p1=" + p1 + ", p2=" + p2 + ", ccy=" + ccy + "]");
        }
        if (p1.compareTo(p2) < 0) {
            this.p1 = p1;
            this.p2 = p2;
        } else {
            this.p1 = p2;
            this.p2 = p1;
        }
        this.ccy = ccy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrustlineKey that = (TrustlineKey) o;

        return p1.equals(that.p1) && p2.equals(that.p2) && ccy.equals(that.ccy);
    }

    @Override
    public int hashCode() {
        int result = p1.hashCode();
        result = 31 * result + p2.hashCode();
        result = 31 * result + ccy.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TrustlineKey{" +
                "p1=" + p1 +
                "p2=" + p2 +
                ", ccy=" + ccy +
                '}';
    }
}
