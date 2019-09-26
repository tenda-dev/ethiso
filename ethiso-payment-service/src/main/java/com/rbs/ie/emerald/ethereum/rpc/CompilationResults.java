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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class CompilationResults implements Iterable<CompilationResult> {
	private Map<String, CompilationResult> compilationResults = new HashMap<>();
	
	@JsonAnySetter
	public void set(final String name, final CompilationResult compilationResult) {
		compilationResults.put(name, compilationResult);
	}
	
	@JsonAnyGetter
	public Map<String, CompilationResult> getcompilationResults() {
		return compilationResults;
	}
	
	public CompilationResult get(final String name) {
		return compilationResults.get(name);
	}
	
	@Override
	public Iterator<CompilationResult> iterator() {
		return compilationResults.values().iterator();
	}
}