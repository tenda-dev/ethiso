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
package com.rbs.ie.emerald.ethereum.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class UFixedTypeConverter implements TypeConverter<BigDecimal> {

	private static final BigDecimal TWO = new BigDecimal("2");
	private final BigDecimal twoPowerN;
	private final TypeConverter<BigInteger> uintTypeConverter;
	private final int m;
	private final int n;

	public UFixedTypeConverter(final int m, final int n) {
		if (m < 8 || n < 8 || m + n > 256 || m % 8 != 0 || n % 8 != 0) {
			throw new IllegalArgumentException(
					"Invalid sizes for " + UFixedTypeConverter.class.getSimpleName() + " [m=" + m + ", n=" + n + "]");
		}

		this.twoPowerN = TWO.pow(n);
		this.uintTypeConverter = new UIntTypeConverter(m + n);
		this.m = m;
		this.n = n;
	}

	@Override
	public void serialiseType(final ByteBuffer byteBuffer, final BigDecimal value) {
		try {
			final BigInteger bigInteger = value.multiply(twoPowerN).toBigIntegerExact();

			uintTypeConverter.serialiseType(byteBuffer, bigInteger);
		} catch (ArithmeticException | IllegalArgumentException e) {
			throw new IllegalArgumentException("Value " + value + " not supported by type fixed" + m + "x" + n);
		}
	}

	@Override
	public BigDecimal deserialise(final ByteBuffer byteBuffer) {
		try {
			final BigInteger bigInteger = uintTypeConverter.deserialise(byteBuffer);

			return new BigDecimal(bigInteger).divide(twoPowerN);
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException("Value not supported by fixed" + m + "x" + n, e);
		}
	}

	@Override
	public Class<BigDecimal> getTypeClass() {
		return BigDecimal.class;
	}
	
	@Override
	public boolean isDynamic() {
		return false;
	}
	
	@Override
	public int getHeadSize() {
		return 32;
	}
}