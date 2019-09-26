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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonRpcResponse extends JsonRpcBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonRpcResponse.class);

	private static final int DEFAULT_ERROR_CODE = -1;

	private static final String RESULT = "result";
	private static final String ERROR = "error";

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Object result;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Error error;

	public JsonRpcResponse() {
		// Do nothing
	}

	public JsonRpcResponse(final int id, final Object result, final Throwable t) {
		if (result != null && t != null) {
			throw new IllegalArgumentException("Either result or t must be null");
		}

		setId(String.valueOf(id));

		if (t != null) {
			final JsonRpcResponse.Error error = new JsonRpcResponse.Error();

			error.setCode(DEFAULT_ERROR_CODE);
			error.setMessage(t.getMessage());
			error.setData(t.getClass().getName());

			setError(error);
		} else {
			setResult(result);
		}
	}

	public Object getResult() {
		return result;
	}

	public <T> T getResultAs(final Class<T> cls, final ObjectMapper objectMapper) throws Throwable {
		if (error != null) {
			if (error != null) {
				LOGGER.debug(objectMapper.writeValueAsString(error));
			}

			if (error.getData() == null) {
				throw new JsonRpcException(error.getMessage(), error.getCode());
			}
			
			try {
				throw Class.forName(String.valueOf(error.getData())).asSubclass(Throwable.class)
						.getConstructor(String.class).newInstance(error.getMessage());
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
					| ClassNotFoundException | NullPointerException e) {
				throw new IllegalStateException("Json rpc error " + objectMapper.writeValueAsString(error), e);
			}
		} else {
			return objectMapper.convertValue(result, cls);
		}
	}

	public void setResult(Object result) {
		this.error = null;
		this.result = result;
	}

	@JsonIgnore
	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		if (error != null) {
			this.result = null;
			this.error = error;
		}
	}

	@JsonAnyGetter
	public Map<String, Object> additionalProperties() {
		final Map<String, Object> additionalProperties = new HashMap<>();

		if (error != null) {
			additionalProperties.put(ERROR, error);
		} else {
			additionalProperties.put(RESULT, result);
		}

		return additionalProperties;
	}

	public static class Error {
		private int code;
		private String message;
		private Object data;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}
	}
}