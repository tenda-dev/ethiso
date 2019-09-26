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

public class BlockInfo {
	private Quantity number;
	private String hash;
	private String parentHash;
	private String nonce;
	private String sha3Uncles;
	private String logsBloom;
	private String transactionsRoot;
	private String stateRoot;
	private String receiptRoot;
	private String miner;
	private String difficulty;
	private String totalDifficulty;
	private String extraData;
	private String size;
	private String gasLimit;
	private String gasUsed;
	private String timestamp;
	private List<Object> transactions;
	private List<String> uncles;
	private String mixHash;
	private String receiptsRoot;


	public Quantity getNumber() {
		return number;
	}

	public void setNumber(Quantity number) {
		this.number = number;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getParentHash() {
		return parentHash;
	}

	public void setParentHash(String parentHash) {
		this.parentHash = parentHash;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getSha3Uncles() {
		return sha3Uncles;
	}

	public void setSha3Uncles(String sha3Uncles) {
		this.sha3Uncles = sha3Uncles;
	}

	public String getLogsBloom() {
		return logsBloom;
	}

	public void setLogsBloom(String logsBloom) {
		this.logsBloom = logsBloom;
	}

	public String getTransactionsRoot() {
		return transactionsRoot;
	}

	public void setTransactionsRoot(String transactionsRoot) {
		this.transactionsRoot = transactionsRoot;
	}

	public String getStateRoot() {
		return stateRoot;
	}

	public void setStateRoot(String stateRoot) {
		this.stateRoot = stateRoot;
	}

	public String getReceiptRoot() {
		return receiptRoot;
	}

	public void setReceiptRoot(String receiptRoot) {
		this.receiptRoot = receiptRoot;
	}

	public String getMiner() {
		return miner;
	}

	public void setMiner(String miner) {
		this.miner = miner;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public String getTotalDifficulty() {
		return totalDifficulty;
	}

	public void setTotalDifficulty(String totalDifficulty) {
		this.totalDifficulty = totalDifficulty;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getGasLimit() {
		return gasLimit;
	}

	public void setGasLimit(String gasLimit) {
		this.gasLimit = gasLimit;
	}

	public String getGasUsed() {
		return gasUsed;
	}

	public void setGasUsed(String gasUsed) {
		this.gasUsed = gasUsed;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public List<Object> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Object> transactions) {
		this.transactions = transactions;
	}

	public List<String> getUncles() {
		return uncles;
	}

	public void setUncles(List<String> uncles) {
		this.uncles = uncles;
	}

	public String getMixHash() {
		return mixHash;
	}

	public void setMixHash(String mixHash) {
		this.mixHash = mixHash;
	}

	public String getReceiptsRoot() {
		return receiptsRoot;
	}

	public void setReceiptsRoot(String receiptsRoot) {
		this.receiptsRoot = receiptsRoot;
	}
}