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
package com.std.ie.jsonrpc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class JsonRpcRequest extends JsonRpcBase {

	private static final List<Object> EMPTY_LIST = Collections.emptyList();
	
	private String method;
	private List<Object> params;

	public JsonRpcRequest() {
		// Do nothing
	}

	public JsonRpcRequest(final int id, final String methodName, final Object... args) {
		setId(String.valueOf(id));
		setMethod(methodName);
		setParams(args != null ? Arrays.asList(args) : null);
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<Object> getParams() {
		return params != null? params : EMPTY_LIST;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "JsonRpcRequest{" +
				"method='" + method + '\'' +
				", params=" + params +
				'}';
	}
}