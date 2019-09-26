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
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonRpcRequestTest {

	private static final String TEST_JSON_RPC_VALUE = "2.0";
	private static final String TEST_ID = "test";
	private static final String TEST_METHOD = "testMethod";
	private static final List<Object> TEST_PARAMS = Arrays.asList("a", 1, true);

	private static String STRING_VALUE = "{\"id\":\"test\",\"method\":\"testMethod\",\"params\":[\"a\",1,true],\"jsonrpc\":\"2.0\"}";
	
	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void serialize() throws JsonProcessingException {
		final JsonRpcRequest jsonRpcRequest = new JsonRpcRequest();

		jsonRpcRequest.setId(TEST_ID);
		jsonRpcRequest.setMethod(TEST_METHOD);
		jsonRpcRequest.setParams(TEST_PARAMS);

		assertEquals(STRING_VALUE,
				objectMapper.writeValueAsString(jsonRpcRequest));
	}
	
	@Test
	public void deserialize() throws IOException {
		final JsonRpcRequest jsonRpcRequest = objectMapper.readValue(STRING_VALUE, JsonRpcRequest.class);

		assertEquals(TEST_JSON_RPC_VALUE, jsonRpcRequest.getJsonRpc());
		assertEquals(TEST_ID, jsonRpcRequest.getId());
		assertEquals(TEST_METHOD, jsonRpcRequest.getMethod());
		assertEquals(TEST_PARAMS, jsonRpcRequest.getParams());
	}
}