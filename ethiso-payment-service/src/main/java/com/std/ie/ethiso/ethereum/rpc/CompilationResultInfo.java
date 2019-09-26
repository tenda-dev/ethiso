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
package com.std.ie.ethiso.ethereum.rpc;

import java.util.List;
import java.util.Map;

public class CompilationResultInfo {
	private String source;
	private String language;
	private String languageVersion;
	private String compilerVersion;
	private List<Function> abiDefinition;
	private Map<String, Object> userDoc;
	private Map<String, Object> developerDoc;
	private String compilerOptions;
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguageVersion() {
		return languageVersion;
	}

	public void setLanguageVersion(String languageVersion) {
		this.languageVersion = languageVersion;
	}

	public String getCompilerVersion() {
		return compilerVersion;
	}

	public void setCompilerVersion(String compilerVersion) {
		this.compilerVersion = compilerVersion;
	}

	public List<Function> getAbiDefinition() {
		return abiDefinition;
	}

	public void setAbiDefinition(List<Function> abiDefinition) {
		this.abiDefinition = abiDefinition;
	}

	public Map<String, Object> getUserDoc() {
		return userDoc;
	}

	public void setUserDoc(Map<String, Object> userDoc) {
		this.userDoc = userDoc;
	}

	public Map<String, Object> getDeveloperDoc() {
		return developerDoc;
	}

	public void setDeveloperDoc(Map<String, Object> developerDoc) {
		this.developerDoc = developerDoc;
	}
	
	public String getCompilerOptions() {
		return compilerOptions;
	}
	
	public void setCompilerOptions(String compilerOptions) {
		this.compilerOptions = compilerOptions;
	}
}