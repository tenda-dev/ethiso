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

import java.util.Map;

public class NodeInfo {
	private String enode;
	private String id;
	private String ip;
	private String listenAddr;
	private String name;
	private Map<String, Integer> ports;
	private Map<String, Protocol> protocols;

	public String getEnode() {
		return enode;
	}

	public void setEnode(String enode) {
		this.enode = enode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getListenAddr() {
		return listenAddr;
	}

	public void setListenAddr(String listenAddr) {
		this.listenAddr = listenAddr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Integer> getPorts() {
		return ports;
	}

	public void setPorts(Map<String, Integer> ports) {
		this.ports = ports;
	}

	public Map<String, Protocol> getProtocols() {
		return protocols;
	}

	public void setProtocols(Map<String, Protocol> protocols) {
		this.protocols = protocols;
	}

	public static class Protocol {
		private Long difficulty;
		private String genesis;
		private String head;
		private Integer network;

		public Long getDifficulty() {
			return difficulty;
		}

		public void setDifficulty(Long difficulty) {
			this.difficulty = difficulty;
		}

		public String getGenesis() {
			return genesis;
		}

		public void setGenesis(String genesis) {
			this.genesis = genesis;
		}

		public String getHead() {
			return head;
		}

		public void setHead(String head) {
			this.head = head;
		}

		public Integer getNetwork() {
			return network;
		}

		public void setNetwork(Integer network) {
			this.network = network;
		}
	}
}