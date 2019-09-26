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
package com.std.ie.ethiso.ethereum.contract;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import com.std.ie.ethiso.ethereum.ContractProxy;
import com.std.ie.ethiso.ethereum.Ethereum;
import com.std.ie.ethiso.ethereum.PaymentServiceMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.ie.ethiso.ethereum.rpc.Address;

public class GlobalRegistrar extends NameToAddressRegistrar {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalRegistrar.class);
	
	private static final String REGISTRAR_ADDRESS_KEY = "registrar.address";
	private static final String PROPERTIES_FILE = "registrar.properties";

	public GlobalRegistrar(final Ethereum ethereum, final ContractProxy.Mode mode, final Address contractAddress) {
		super(ethereum, mode, checkAddress(contractAddress));
		
		// If we weren't passed an address then store the actual contract address
		if (contractAddress.isZero()) {
			storeRegistrarAddress(getContractAddress());
		}
	}

	private static Address checkAddress(final Address address) {
		Address contractAdress = address;
		
		if (contractAdress.isZero()) {
			contractAdress = getStoredRegistrarAddress();
		}
		
		return contractAdress;
	}
	
	private static Address getStoredRegistrarAddress() {
		final Properties properties = new Properties();

		try (final InputStream inputStream = new FileInputStream(PROPERTIES_FILE)) {
			properties.load(inputStream);
		} catch (final FileNotFoundException e) {
			LOGGER.warn("No " + PROPERTIES_FILE + " exists");
		} catch (final IOException e) {
			LOGGER.warn("Error reading " + PROPERTIES_FILE, e);
		}

		return new Address(properties.getProperty(REGISTRAR_ADDRESS_KEY));
	}

	private static void storeRegistrarAddress(final Address registrarAddress) {
		final Properties properties = new Properties();

		properties.put(REGISTRAR_ADDRESS_KEY, registrarAddress.toString());

		try (final OutputStream outputStream = new FileOutputStream(PROPERTIES_FILE)) {
			properties.store(outputStream,
					"Written by " + PaymentServiceMain.class.getSimpleName() + " at " + new Date());
		} catch (final IOException e) {
			LOGGER.warn("Failed to store " + PROPERTIES_FILE);
		}
	}
}