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
package com.iso.ie.ethiso.ethereum;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iso.ie.ethiso.api.Configuration;
import com.iso.ie.ethiso.api.ConfigurationService;

public class PaymentServiceMainTest {

	private static final String TEST_ENODE = "testEnode";
	private static final String TEST_ADDRESS = "testAddress";
	private String confUrl;
	private ConfigurationService configurationService;

	@Before
	public void setUp() throws IOException {
		final Configuration configuration = new Configuration();

		configuration.setContractRegistrar(TEST_ADDRESS);
		configuration.setEnode(TEST_ENODE);

		final File tempFile = Files.createTempFile("conf", "").toFile();

		tempFile.deleteOnExit();

		new ObjectMapper().writeValue(tempFile, configuration);

		confUrl = tempFile.toURI().toURL().toExternalForm();

		configurationService = new StaticConfigurationService();
	}

	@Test
	public void getConfiguration() {
		final Configuration configuration = configurationService.getConfiguration(confUrl);

		assertEquals(TEST_ENODE, configuration.getEnode());
		assertEquals(TEST_ADDRESS, configuration.getContractRegistrar());
	}
}