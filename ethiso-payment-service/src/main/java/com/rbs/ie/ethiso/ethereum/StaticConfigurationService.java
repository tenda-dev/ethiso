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
package com.rbs.ie.ethiso.ethereum;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbs.ie.ethiso.api.Configuration;
import com.rbs.ie.ethiso.api.ConfigurationService;

/**
 * A {@link ConfigurationService} that directly retrieves a
 * {@link Configuration} object from the given {@link URL}.
 */
public class StaticConfigurationService implements ConfigurationService {
	@Override
	public Configuration getConfiguration(final String url) {
		try {
			return new ObjectMapper().readValue(new URL(url).openStream(), Configuration.class);
		} catch (final MalformedURLException e) {
			throw new IllegalArgumentException(e);
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}
}