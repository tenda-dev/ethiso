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

import java.util.ArrayList;
import java.util.List;

public class Filter {
	private String fromBlock = "earliest";
	private String toBlock;
	private String address;
	private List<String> topics = new ArrayList<>();

	public String getFromBlock() {
		return fromBlock;
	}

	public void setFromBlock(String fromBlock) {
		this.fromBlock = fromBlock;
	}

	public String getToBlock() {
		return toBlock;
	}

	public void setToBlock(String toBlock) {
		this.toBlock = toBlock;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Filter [fromBlock=");
		builder.append(fromBlock);
		builder.append(", toBlock=");
		builder.append(toBlock);
		builder.append(", address=");
		builder.append(address);
		builder.append(", topics=");
		builder.append(topics);
		builder.append("]");
		return builder.toString();
	}
}