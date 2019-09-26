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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.std.ie.ethiso.api.ConfigurationService;
import com.std.ie.ethiso.ethereum.rpc.Address;

public class ConfigurationServiceImplTest {

	private static final String ENODE = "enode://1234@localhost:1234/";
	private static final Address ACCOUNT = new Address("0x0123456789012345678901234567890123456789");

	private ConfigurationService configurationService;

	@Before
	public void setUp() {
		final Ethereum ethereum = mock(Ethereum.class);

		when(ethereum.getEnode()).thenReturn(ENODE);
		when(ethereum.getFromAccount()).thenReturn(ACCOUNT);

		configurationService = new ConfigurationServiceImpl(ethereum, null);
	}

	@Test
	public void getConfigurationInternal() {
		assertEquals(ENODE, configurationService.getConfiguration("http://localhost:1234/config").getEnode());
	}

	@Test
	public void getConfigurationExternal() {
		assertEquals("enode://1234@169.254.0.1:1239/",
				configurationService.getConfiguration("http://169.254.0.1:1238/config").getEnode());
	}
}