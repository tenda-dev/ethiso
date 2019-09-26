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
package com.std.ie.ethiso.ethereum;

import static com.std.ie.ethiso.ethereum.EthereumUtils.getFunctionSignatureHash;
import static com.std.ie.ethiso.ethereum.EthereumUtils.getTypeConverter;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.std.ie.ethiso.ethereum.rpc.Function;
import com.std.ie.ethiso.ethereum.rpc.LogEntry;
import com.std.ie.ethiso.ethereum.rpc.Parameter;
import com.std.ie.ethiso.ethereum.types.TypeConverter;
import com.std.ie.ethiso.ethereum.types.UIntTypeConverter;

public class FunctionEncoding {

	private static final String FALLBACK = "fallback";
	private static final String CONSTRUCTOR = "constructor";

	private static final TypeConverter<BigInteger> DYNAMIC_OFFSET_TYPE_CONVERTER = new UIntTypeConverter(256);
	private final String hash;
	private final List<TypeConverter<?>> eventIndexedParameters = new ArrayList<>();
	private final List<TypeConverter<?>> inputParameters = new ArrayList<>();
	private final List<TypeConverter<?>> outputParamaters = new ArrayList<>();
	private final int inputParametersHeadSize;

	public FunctionEncoding(final Function function) {
		this.hash = function.getType().equals(CONSTRUCTOR)? "" : getFunctionSignatureHash(function);

		int headSize = 0;

		if (function.getInputs() != null) {
			for (final Parameter parameter : function.getInputs()) {
				if (parameter.isIndexed()) {
					eventIndexedParameters.add(getTypeConverter(parameter.getType()));
				} else {
					final TypeConverter<?> typeConverter = getTypeConverter(parameter.getType());

					inputParameters.add(typeConverter);

					headSize += typeConverter.getHeadSize();
				}
			}
		}

		this.inputParametersHeadSize = headSize;

		if (function.getOutputs() != null) {
			for (final Parameter parameter : function.getOutputs()) {
				outputParamaters.add(EthereumUtils.getTypeConverter(parameter.getType()));
			}
		}
	}

	public String encode(final Object... args) {
		final ByteBuffer headBuffer = ByteBuffer.allocateDirect(inputParametersHeadSize);

		final ByteBuffer tailBuffer = ByteBuffer.allocateDirect(1024);

		if (args.length != inputParameters.size()) {
			throw new IllegalArgumentException(
					"Wrong number of parameters, expected " + inputParameters.size() + ", actual " + args.length);
		}

		int idx = 0;

		for (final TypeConverter<?> typeConverter : inputParameters) {
			if (typeConverter.isDynamic()) {
				final int offset = inputParametersHeadSize + tailBuffer.position();

				typeConverter.serialise(tailBuffer, args[idx++]);

				DYNAMIC_OFFSET_TYPE_CONVERTER.serialise(headBuffer, new BigInteger(String.valueOf(offset)));
			} else {
				typeConverter.serialise(headBuffer, args[idx++]);
			}
		}

		return hash.substring(0, Math.min(hash.length(), 10)) + encodeBuffer(headBuffer) + encodeBuffer(tailBuffer);
	}

	public Object[] decode(final String result) {
		final ByteBuffer byteBuffer = decodeBuffer(result);

		byteBuffer.flip();

		final Object[] resultArray = new Object[outputParamaters.size()];

		int idx = 0;

		for (final TypeConverter<?> typeConverter : outputParamaters) {
			resultArray[idx++] = typeConverter.deserialise(byteBuffer);
		}

		return resultArray;
	}

	public Object[] decodeEvent(final LogEntry logEntry) {
		final Object[] resultArray = new Object[eventIndexedParameters.size() + inputParameters.size()];

		int idx = 0;

		for (final TypeConverter<?> typeConverter : eventIndexedParameters) {
			final ByteBuffer byteBuffer = decodeBuffer(logEntry.getTopics().get(idx + 1));

			byteBuffer.flip();

			resultArray[idx] = typeConverter.deserialise(byteBuffer);

			idx++;
		}

		final ByteBuffer byteBuffer = decodeBuffer(logEntry.getData());

		byteBuffer.flip();

		for (final TypeConverter<?> typeConverter : inputParameters) {
			if (typeConverter.isDynamic()) {
				final BigInteger offset = DYNAMIC_OFFSET_TYPE_CONVERTER.deserialise(byteBuffer);

				byteBuffer.mark();

				byteBuffer.position(offset.intValue());

				resultArray[idx++] = typeConverter.deserialise(byteBuffer);

				byteBuffer.reset();
			} else {
				resultArray[idx++] = typeConverter.deserialise(byteBuffer);
			}
		}

		return resultArray;
	}

	public String getHash() {
		return hash;
	}

	private String encodeBuffer(final ByteBuffer buffer) {
		final StringBuilder stringBuilder = new StringBuilder();

		buffer.flip();

		while (buffer.hasRemaining()) {
			final String byteString = Integer.toHexString(buffer.get() & 0xFF);

			if (byteString.length() == 1) {
				stringBuilder.append('0');
			}
			stringBuilder.append(byteString);
		}

		return stringBuilder.toString();
	}

	private ByteBuffer decodeBuffer(final String string) {
		final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(string.length() / 2);

		if (!string.startsWith("0x")) {
			throw new IllegalArgumentException("Invalid string format");
		}

		int idx = 2;

		while (idx < string.length() - 1) {
			byteBuffer.put((byte) Integer.parseInt(string.substring(idx, idx + 2), 16));

			idx += 2;
		}

		return byteBuffer;
	}

	static Map<String, FunctionEncoding> getFunctionEncodings(final List<Function> abiDefinition) {
		final Map<String, FunctionEncoding> functionEncodings = new HashMap<>();

		for (final Function function : abiDefinition) {

			if (!function.getType().equals(FALLBACK)) {
				functionEncodings.put(function.getName(), new FunctionEncoding(function));
			}
		}

		return Collections.unmodifiableMap(functionEncodings);
	}
}