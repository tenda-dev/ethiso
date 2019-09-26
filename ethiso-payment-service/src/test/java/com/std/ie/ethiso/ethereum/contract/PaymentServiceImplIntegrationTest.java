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

import static com.std.ie.ethiso.DockerUtils.getDockerHostName;
import static com.std.ie.ethiso.ethereum.ContractProxy.Mode.INSTALL;
import static com.std.ie.ethiso.ethereum.TestConstants.ADDITIONAL_ACCOUNT;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.std.ie.ethiso.PaymentService;
import com.std.ie.ethiso.domain.Bic;
import com.std.ie.ethiso.domain.Currency;
import com.std.ie.ethiso.domain.Iban;
import com.std.ie.ethiso.domain.Party;
import com.std.ie.ethiso.ethereum.Ethereum;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerException;

public class PaymentServiceImplIntegrationTest {

	private static final Party TEST_PARTY = Party.party("0x0123456789012345678901234567890123456789");

	private static final Iban TEST_IBAN = Iban.iban("test");

	private static final Bic TEST_BIC = Bic.bic("test");

	private static final String PASSWORD = "wibble";

	private static Ethereum ethereum;
	private static PaymentService paymentService;
	private static NameToAddressRegistrar bicRegistrar;
	
	private static final Properties properties = new Properties();

	static {
		((Jdk14Logger) LogFactory.getLog("org.apache.http")).getLogger().setLevel(Level.WARNING);
	}

	@BeforeClass
	public static void setUp() throws IOException, InterruptedException, DockerCertificateException, DockerException, TimeoutException {
		ethereum = new Ethereum(PASSWORD, InetSocketAddress.createUnresolved(getDockerHostName(), getGethPort()), 1,
 				ADDITIONAL_ACCOUNT);

		ethereum.startMining();

		bicRegistrar = new NameToAddressRegistrar(ethereum, INSTALL, null);
		paymentService = new PaymentServiceImpl(ethereum, INSTALL, null, bicRegistrar);
	}

	@Test
	public void setAllow() {
		paymentService.setAllow(TEST_PARTY, Currency.GBP, new BigInteger("100"));
	}

	@Test
	public void setLimit() {
		paymentService.setLimit(TEST_PARTY, Currency.GBP, new BigInteger("100"));
	}

	@Test(expected = IllegalStateException.class)
	public void sendPaymentFailsPayOurselves() {
		paymentService.sendPayment(TEST_BIC, TEST_BIC, TEST_IBAN, TEST_IBAN, Currency.GBP, new BigInteger("100"), Collections.emptyMap());
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
