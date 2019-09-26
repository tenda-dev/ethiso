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

class StringType implements CharSequence, Comparable<StringType> {

    private final String value;

    public StringType(final String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Invalid value [" + value + "] for [" + this.getClass().getSimpleName() + "]");
        }

        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        boolean equals = false;

        if (this == obj) {
            equals = true;
        } else if (obj != null) {
            equals = getClass().equals(obj.getClass()) && value.equals(((StringType) obj).value);
        }

        return equals;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(final int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return value.subSequence(start, end);
    }

    @Override
    public int compareTo(final StringType o) {
        int compareTo = 1;

        if (o != null) {
            compareTo = getClass().getName().compareTo(o.getClass().getName());

            if (compareTo == 0) {
                compareTo = value.compareTo(o.value);
            }
        }

        return compareTo;
    }
}