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

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.ie.ethiso.api.Configuration;
import com.std.ie.ethiso.api.ConfigurationService;

public class ConfigurationServiceImpl implements ConfigurationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

	private final Configuration configuration;
	

	public ConfigurationServiceImpl(final Ethereum ethereum, final String contractRegistrar) {
		this.configuration = toConfiguration(ethereum, contractRegistrar);
	}

	@Override
	public Configuration getConfiguration(final String url) {
		Configuration actualConfiguration = configuration;
		
		try {
			final URL configServiceUrl = new URL(url);

			final InetAddress clientViewOfIP = InetAddress.getByName(configServiceUrl.getHost());
				
			if (!clientViewOfIP.isSiteLocalAddress() && !clientViewOfIP.isLoopbackAddress()) {
				LOGGER.info("Client address is not site local or loopback [" + clientViewOfIP + "]");

				final String enodeString = actualConfiguration.getEnode();
				
				LOGGER.info("Received enode: {}", enodeString);
				
				final URI enode = new URI(actualConfiguration.getEnode());

				final URI newEnode = new URI(enode.getScheme(), enode.getUserInfo(), clientViewOfIP.getHostAddress(),
						configServiceUrl.getPort() + 1, enode.getPath(), enode.getQuery(), enode.getFragment());

				LOGGER.info("Replaced enode [" + enode + "] with [" + newEnode + "]");

				actualConfiguration = new Configuration(configuration);
				actualConfiguration.setEnode(newEnode.toString());
			}
		} catch (MalformedURLException | UnknownHostException | URISyntaxException e) {
			LOGGER.warn("Failed replacing IP [" + e + "]");
			// Let's ignore this, just return enode unchanged
		}

		return actualConfiguration;
	}

	private static Configuration toConfiguration(final Ethereum ethereum, final String contractRegistrar) {
		final Configuration configuration = new Configuration();

		configuration.setEnode(ethereum.getEnode());
		configuration.setContractRegistrar(contractRegistrar);

		return configuration;
	}
}