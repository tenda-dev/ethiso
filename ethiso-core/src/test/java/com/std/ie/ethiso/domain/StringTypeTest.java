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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringTypeTest {

    public static final String TEST_STRING = "test";
    public static final StringType TEST_STRING_TYPE = new StringType(TEST_STRING);
    public static final StringType TEST_STRING_TYPE_EQUAL = new StringType(TEST_STRING);
    public static final StringType TEST_STRING_TYPE_NOT_EQUAL = new StringType("wibble");
    public static final StringType TEST_STRING_TYPE_DERIVED = new DerivedStringType(TEST_STRING);
    public static final StringType TEST_STRING_TYPE_DERIVED_EQUAL = new DerivedStringType(TEST_STRING);

    @Test
    public void hashCodeEqualsStringHashCode() {
        assertEquals(TEST_STRING.hashCode(), new StringType("test").hashCode());
    }

    @Test
    public void equals() {
        // Itself
        assertEquals(true, TEST_STRING_TYPE.equals(TEST_STRING_TYPE));
        // Same value
        assertEquals(true, TEST_STRING_TYPE.equals(TEST_STRING_TYPE_EQUAL));
        assertEquals(true, TEST_STRING_TYPE_EQUAL.equals(TEST_STRING_TYPE));
        // Different value
        assertEquals(false, TEST_STRING_TYPE.equals(TEST_STRING_TYPE_NOT_EQUAL));
        assertEquals(false, TEST_STRING_TYPE_NOT_EQUAL.equals(TEST_STRING_TYPE_EQUAL));
        // Derived types (not equal)
        assertEquals(false, TEST_STRING_TYPE.equals(TEST_STRING_TYPE_DERIVED));
        assertEquals(false, TEST_STRING_TYPE_DERIVED.equals(TEST_STRING_TYPE));
        // Derived type is equal to itself
        assertEquals(true, TEST_STRING_TYPE_DERIVED.equals(TEST_STRING_TYPE_DERIVED));
        // Derived, same value
        assertEquals(true, TEST_STRING_TYPE_DERIVED.equals(TEST_STRING_TYPE_DERIVED_EQUAL));
    }

    @Test
    public void compareTo() {
        // null's come first
        assertEquals(true, TEST_STRING_TYPE.compareTo(null) > 0);
        // Consistent with equals
        consistentWithEquals(TEST_STRING_TYPE, TEST_STRING_TYPE);
        consistentWithEquals(TEST_STRING_TYPE, TEST_STRING_TYPE_EQUAL);
        consistentWithEquals(TEST_STRING_TYPE, TEST_STRING_TYPE_NOT_EQUAL);
        consistentWithEquals(TEST_STRING_TYPE, TEST_STRING_TYPE_DERIVED);
        consistentWithEquals(TEST_STRING_TYPE_DERIVED, TEST_STRING_TYPE_DERIVED_EQUAL);
    }

    private static void consistentWithEquals(final StringType s1, final StringType s2) {
        assertEquals(s1.equals(s2), s1.compareTo(s2) == 0);
    }
    private static class DerivedStringType extends StringType {
        public DerivedStringType(final String value) {
            super(value);
        }
    }
}
