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
package com.iso.ie.ethiso.ethereum.rpc;

import java.util.List;

public class TransactionReceipt {
	private String transactionHash;
	private String transactionIndex;
	private String blockHash;
	private String blockNumber;
	private String cumulativeGasUsed;
	private String gasUsed;
	private Address contractAddress;
	private List<LogEntry> logs;
	private String from;
	private String root;
	private String to;
	private String logsBloom;

	public String getTransactionHash() {
		return transactionHash;
	}

	public void setTransactionHash(String transactionHash) {
		this.transactionHash = transactionHash;
	}

	public String getTransactionIndex() {
		return transactionIndex;
	}

	public void setTransactionIndex(String transactionIndex) {
		this.transactionIndex = transactionIndex;
	}

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}

	public String getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(String blockNumber) {
		this.blockNumber = blockNumber;
	}

	public String getCumulativeGasUsed() {
		return cumulativeGasUsed;
	}

	public void setCumulativeGasUsed(String cumulativeGasUsed) {
		this.cumulativeGasUsed = cumulativeGasUsed;
	}

	public String getGasUsed() {
		return gasUsed;
	}

	public void setGasUsed(String gasUsed) {
		this.gasUsed = gasUsed;
	}

	public Address getContractAddress() {
		return contractAddress;
	}

	public void setContractAddress(Address contractAddress) {
		this.contractAddress = contractAddress;
	}

	public List<LogEntry> getLogs() {
		return logs;
	}

	public void setLogs(List<LogEntry> logs) {
		this.logs = logs;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getLogsBloom() {
		return logsBloom;
	}

	public void setLogsBloom(String logsBloom) {
		this.logsBloom = logsBloom;
	}
}