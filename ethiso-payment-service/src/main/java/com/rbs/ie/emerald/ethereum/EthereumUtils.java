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
package com.rbs.ie.emerald.ethereum;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.rbs.ie.emerald.ethereum.rpc.Function;
import com.rbs.ie.emerald.ethereum.rpc.Parameter;
import com.rbs.ie.emerald.ethereum.types.TypeConverter;
import com.rbs.ie.emerald.ethereum.types.TypeConverterManager;
import com.rbs.ie.emerald.ethereum.types.TypeConverterManagerImpl;

public class EthereumUtils {

	private static final String DIGEST_ALGORITHM = "KECCAK-256";

	private static final TypeConverterManager TYPE_CONVERTER_MANAGER = new TypeConverterManagerImpl();

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	static TypeConverter<?> getTypeConverter(final String type) {
		return TYPE_CONVERTER_MANAGER.getTypeConverter(type);
	}

	static String getFunctionSignatureHash(final Function function) {
		try {
			final byte[] signature = getCanonicalSignature(function);

			final MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);

			return toHexString(messageDigest.digest(signature));
		} catch (final NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	static byte[] getCanonicalSignature(final Function function) {
		
		if (function.getName() == null) {
			throw new IllegalArgumentException();
		}

		final StringBuilder str = new StringBuilder();

		str.append(function.getName());
		str.append('(');
		if (function.getInputs() != null) {
			boolean first = true;
			for (final Parameter parameter : function.getInputs()) {
				if (!first) {
					str.append(',');
				}
				str.append(parameter.getType());
				first = false;
			}
		}
		str.append(')');

		return str.toString().getBytes();
	}

	private static String toHexString(final byte[] bytes) {
		final StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("0x");
		
		for (int i = 0; i < bytes.length; i++) {
			final String b = Integer.toHexString(bytes[i] & 0xFF);
			
			if (b.length() == 1) {
				stringBuilder.append('0');
			}
			
			stringBuilder.append(b);
		}
		
		return stringBuilder.toString();
	}
}