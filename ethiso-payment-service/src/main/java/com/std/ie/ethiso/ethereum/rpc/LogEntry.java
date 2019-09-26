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
import java.util.Objects;

public class LogEntry {
	private boolean removed;
	private Quantity logIndex;
	private Quantity transactionIndex;
	private String transactionHash;
	private String blockHash;
	private Quantity blockNumber;
	private Address address;
	private String data;
	private List<String> topics;

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public Quantity getLogIndex() {
		return logIndex;
	}

	public void setLogIndex(Quantity logIndex) {
		this.logIndex = logIndex;
	}

	public Quantity getTransactionIndex() {
		return transactionIndex;
	}

	public void setTransactionIndex(Quantity transactionIndex) {
		this.transactionIndex = transactionIndex;
	}

	public String getTransactionHash() {
		return transactionHash;
	}

	public void setTransactionHash(String transactionHash) {
		this.transactionHash = transactionHash;
	}

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}

	public Quantity getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(Quantity blockNumber) {
		this.blockNumber = blockNumber;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	@Override
	public int hashCode() {
		return Objects.hash(blockHash, transactionHash, transactionIndex, logIndex);
	}

	@Override
	public boolean equals(final Object that) {
		boolean equals = false;

		if (that == this) {
			equals = true;
		} else if (that instanceof LogEntry) {
			final LogEntry thatLogEntry = (LogEntry) that;

			equals = thatLogEntry.blockHash.equals(this.blockHash)
					&& thatLogEntry.transactionHash.equals(this.transactionHash)
					&& thatLogEntry.transactionIndex.equals(this.transactionIndex)
					&& thatLogEntry.logIndex.equals(this.logIndex);
		}

		return equals;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LogEntry [removed=");
		builder.append(removed);
		builder.append(", logIndex=");
		builder.append(logIndex);
		builder.append(", transactionIndex=");
		builder.append(transactionIndex);
		builder.append(", transactionHash=");
		builder.append(transactionHash);
		builder.append(", blockHash=");
		builder.append(blockHash);
		builder.append(", blockNumber=");
		builder.append(blockNumber);
		builder.append(", address=");
		builder.append(address);
		builder.append(", data=");
		builder.append(data);
		builder.append(", topics=");
		builder.append(topics);
		builder.append("]");
		return builder.toString();
	}
}