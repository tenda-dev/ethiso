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
package com.iso.ie.ethiso.api;

public class Configuration {
	private String enode;
	private String contractRegistrar;

	public Configuration() {
		// Do nothing
	}
	
	public Configuration(final Configuration configuration){	
		this.enode = configuration.enode;
		this.contractRegistrar = configuration.contractRegistrar;
	}
	
	public String getEnode() {
		return enode;
	}

	public void setEnode(String enode) {
		this.enode = enode;
	}

	public String getContractRegistrar() {
		return contractRegistrar;
	}
	
	public void setContractRegistrar(String contractRegistrar) {
		this.contractRegistrar = contractRegistrar;
	}
}