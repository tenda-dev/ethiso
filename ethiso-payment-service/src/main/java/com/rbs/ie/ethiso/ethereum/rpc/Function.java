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
package com.rbs.ie.ethiso.ethereum.rpc;

import java.util.List;

public class Function {
	private boolean constant;
	private List<Parameter> inputs;
	private String name;
	private List<Parameter> outputs;
	private String type;
	private boolean anonymous;
	private boolean payable;
	private boolean fallback;

	public boolean isConstant() {
		return constant;
	}

	public void setConstant(boolean constant) {
		this.constant = constant;
	}

	public List<Parameter> getInputs() {
		return inputs;
	}

	public void setInputs(List<Parameter> inputs) {
		this.inputs = inputs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Parameter> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<Parameter> outputs) {
		this.outputs = outputs;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	
	
	public boolean isFallback() {
		return fallback;
	}

	public void setFallback(boolean fallback) {
		this.fallback = fallback;
	}

	public boolean isPayable() {
		return payable;
	}

	public void setPayable(boolean payable) {
		this.payable = payable;
	}
}