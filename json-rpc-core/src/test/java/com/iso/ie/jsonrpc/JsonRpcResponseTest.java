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
package com.iso.ie.jsonrpc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonRpcResponseTest {

	private static final String TEST_JSON_RPC_VALUE = "2.0";
	private static final String TEST_ID = "test";
	private static final Map<String, Integer> TEST_RESULT = Collections.singletonMap("a", 1);
	private static final int TEST_ERROR_CODE = 1234;
	private static final String TEST_ERROR_MESSAGE = "error message";

	private static String STRING_VALUE_WITH_RESULT = "{\"id\":\"test\",\"jsonrpc\":\"2.0\",\"result\":{\"a\":1}}";
	private static String STRING_VALUE_WITH_ERROR = "{\"id\":\"test\",\"jsonrpc\":\"2.0\",\"error\":{\"code\":1234,\"message\":\"error message\",\"data\":{\"a\":1}}}";

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void serializeWithResult() throws JsonProcessingException {
		final JsonRpcResponse jsonRpcResponse = new JsonRpcResponse();

		jsonRpcResponse.setId(TEST_ID);
		jsonRpcResponse.setResult(TEST_RESULT);

		assertEquals(STRING_VALUE_WITH_RESULT, objectMapper.writeValueAsString(jsonRpcResponse));
	}

	@Test
	public void deserializeWithResult() throws IOException {
		final JsonRpcResponse jsonRpcResponse = objectMapper.readValue(STRING_VALUE_WITH_RESULT, JsonRpcResponse.class);

		assertEquals(TEST_JSON_RPC_VALUE, jsonRpcResponse.getJsonRpc());
		assertEquals(TEST_ID, jsonRpcResponse.getId());
		assertEquals(TEST_RESULT, jsonRpcResponse.getResult());
	}

	@Test
	public void serializeWithError() throws JsonProcessingException {
		final JsonRpcResponse.Error error = new JsonRpcResponse.Error();

		error.setCode(TEST_ERROR_CODE);
		error.setMessage(TEST_ERROR_MESSAGE);
		error.setData(TEST_RESULT);

		final JsonRpcResponse jsonRpcResponse = new JsonRpcResponse();

		jsonRpcResponse.setId(TEST_ID);
		jsonRpcResponse.setError(error);

		assertEquals(STRING_VALUE_WITH_ERROR, objectMapper.writeValueAsString(jsonRpcResponse));
	}

	@Test
	public void deserializeWithError() throws IOException {
		final JsonRpcResponse jsonRpcResponse = objectMapper.readValue(STRING_VALUE_WITH_ERROR, JsonRpcResponse.class);

		assertEquals(TEST_JSON_RPC_VALUE, jsonRpcResponse.getJsonRpc());
		assertEquals(TEST_ID, jsonRpcResponse.getId());
		assertEquals(TEST_ERROR_CODE, jsonRpcResponse.getError().getCode());
		assertEquals(TEST_ERROR_MESSAGE, jsonRpcResponse.getError().getMessage());
		assertEquals(TEST_RESULT, jsonRpcResponse.getError().getData());
	}
}