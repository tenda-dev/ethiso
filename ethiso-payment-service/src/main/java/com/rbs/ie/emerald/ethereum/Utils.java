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

import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.slf4j.Logger;

public class Utils {

	static final int DEFAULT_SLEEP_MILLIS = 1000;
	static final long DEFAULT_TIMEOUT = 0;

	private Utils() {
		// Prevent instantiation
	}

	static <T> T waitFor(final Supplier<T> loopWhileNull, final String message, final Logger logger)
			throws InterruptedException, TimeoutException {
		return waitFor(loopWhileNull, message, logger, DEFAULT_TIMEOUT);
	}

	static <T> T waitFor(final Supplier<T> loopWhileNull, final String message, final Logger logger,
			final long timeoutMillis) throws InterruptedException, TimeoutException {
		return waitFor(loopWhileNull, message, logger, timeoutMillis, DEFAULT_SLEEP_MILLIS);
	}

	static <T> T waitFor(final Supplier<T> loopWhileNull, final String message, final Logger logger,
			final long timeoutMillis, final long sleepMillis) throws InterruptedException, TimeoutException {
		final long start = System.currentTimeMillis();

		T value = loopWhileNull.get();

		while (value == null) {
			final long elapsed = System.currentTimeMillis() - start;

			if (timeoutMillis > 0 && elapsed > timeoutMillis) {
				throw new TimeoutException();
			}

			logger.info(message + " (total " + elapsed / 1000 + " seconds)");

			Thread.sleep(sleepMillis);

			value = loopWhileNull.get();
		}

		return value;
	}
}