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

import static com.rbs.ie.emerald.DockerUtils.getDockerHostName;
import static com.rbs.ie.emerald.ethereum.FunctionEncoding.getFunctionEncodings;
import static com.rbs.ie.emerald.ethereum.TestConstants.ADDITIONAL_ACCOUNT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rbs.ie.emerald.ethereum.contract.NameToAddressRegistrarIntegrationTest;
import com.rbs.ie.emerald.ethereum.rpc.CompilationResult;
import com.rbs.ie.emerald.ethereum.rpc.CompilationResults;
import com.rbs.ie.emerald.ethereum.rpc.TransactionReceipt;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerException;

public class EthereumIntegrationTest {

	private static final BigInteger ADD_ARG2 = new BigInteger("2");
	private static final BigInteger ADD_ARG1 = new BigInteger("1");
	private static final BigInteger SUM = new BigInteger("3");
	private static final BigInteger CONSTRUCTOR_ARG = new BigInteger("4");

	private static final String TEST_CONTRACT = "TestContract";

	private static final String CONSTRUCT_FUNCTION = "Construct";
	private static final String ADD_FUNCTION = "add";
	private static final String SUM_EVENT = "Sum";

	private static final String PASSWORD = "wibble";

	private static Ethereum ethereum;
	private static final Properties properties = new Properties();
	
	static {
		((Jdk14Logger) LogFactory.getLog("org.apache.http")).getLogger().setLevel(Level.WARNING);
	}

	@BeforeClass
	public static void setUp() throws IOException, InterruptedException, DockerCertificateException, DockerException {
		ethereum = new Ethereum(PASSWORD, InetSocketAddress.createUnresolved(getDockerHostName(),
				getGethPort()), 1, ADDITIONAL_ACCOUNT);
	}

	@Test
	public void getFromAccount() {
		assertNotNull(ethereum.getFromAccount());
	}

	@Test
	public void startMiningCompileAndRunContract() throws InterruptedException, ExecutionException, TimeoutException {
		ethereum.startMining();

		final CompilationResults compilationResults = ethereum.compileContract("/TestContract.sol");

		final CompilationResult compilationResult = compilationResults.getcompilationResults().get(TEST_CONTRACT);

		final Map<String, FunctionEncoding> functionEncodings = getFunctionEncodings(compilationResult.getInfo().getAbiDefinition());

		assertEquals(4, functionEncodings.size());
		assertNotNull(functionEncodings.get(ADD_FUNCTION));
		assertNotNull(functionEncodings.get(CONSTRUCT_FUNCTION));
		// Constructor
		assertNotNull(functionEncodings.get(null));
		assertNotNull(functionEncodings.get(SUM_EVENT));

		final String code = compilationResult.getCode();

		final CompletableFuture<BigInteger> constructEventResult = new CompletableFuture<>();

		ethereum.addEventListener(functionEncodings.get(CONSTRUCT_FUNCTION), (x) -> {
			try {
				constructEventResult.complete((BigInteger) x[0]);
			} catch (final RuntimeException e) {
				constructEventResult.completeExceptionally(e);
			}
		});

		final TransactionReceipt transactionReceipt = ethereum
				.newContract(code, functionEncodings.get(null), CONSTRUCTOR_ARG).toCompletableFuture().get();

		assertEquals(CONSTRUCTOR_ARG, constructEventResult.get(30, TimeUnit.SECONDS));

		final CompletableFuture<BigInteger> sumEventResult = new CompletableFuture<>();

		ethereum.addEventListener(functionEncodings.get(SUM_EVENT), (x) -> {
			try {
				sumEventResult.complete((BigInteger) x[0]);
			} catch (final RuntimeException e) {
				sumEventResult.completeExceptionally(e);
			}
		});

		ethereum.sendTransaction(transactionReceipt.getContractAddress(), functionEncodings.get(ADD_FUNCTION), ADD_ARG1,
				ADD_ARG2);

		assertEquals(SUM, sumEventResult.get(30, TimeUnit.SECONDS));

		ethereum.stopMining();
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