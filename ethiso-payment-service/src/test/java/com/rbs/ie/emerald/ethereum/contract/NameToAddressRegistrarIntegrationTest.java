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
package com.rbs.ie.emerald.ethereum.contract;

import static com.rbs.ie.emerald.DockerUtils.getDockerHostName;
import static com.rbs.ie.emerald.ethereum.TestConstants.ADDITIONAL_ACCOUNT;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import javax.naming.NamingSecurityException;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rbs.ie.emerald.ethereum.ContractProxy.Mode;
import com.rbs.ie.emerald.ethereum.Ethereum;
import com.rbs.ie.emerald.ethereum.rpc.Address;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerException;

public class NameToAddressRegistrarIntegrationTest {

	private static final String PASSWORD = "wibble";

	private static final Address ADDRESS = new Address("0x0123456789012345678901234567890123456789");

	private static Ethereum ethereum;
	private static NameToAddressRegistrar registrar;
	
	private static final Properties properties = new Properties();

	static {
		((Jdk14Logger) LogFactory.getLog("org.apache.http")).getLogger().setLevel(Level.WARNING);
	}

	@BeforeClass
	public static void setUp()
			throws IOException, InterruptedException, DockerCertificateException, DockerException, TimeoutException {
		
		ethereum = new Ethereum(PASSWORD, InetSocketAddress.createUnresolved(getDockerHostName(), getGethPort()), 1,
				ADDITIONAL_ACCOUNT);

		ethereum.startMining();

		registrar = new NameToAddressRegistrar(ethereum, Mode.INSTALL, null);
	}

	@Test
	public void shouldBeAbleToRegisterBic() {
		registrar.setAddress("something", ADDRESS);
		assertThat(registrar.getAddress("something"), is(ADDRESS));
	}

	@Test
	public void shouldReturnZeroAddressIfNothingRegistered() {
		assertThat(registrar.getAddress("not registered").isZero(), is(true));
	}

	@AfterClass
	public static void tearDown() throws IOException {
		ethereum.close();
	}
	
	private static int getGethPort() throws IOException{
		properties.load(NameToAddressRegistrarIntegrationTest.class.getResourceAsStream("/port.properties"));
		return Integer.valueOf(properties.getProperty("geth.a.port"));
	}
}