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
package com.rbs.ie.emerald.ethereum.rpc;

import java.util.List;
import java.util.Map;

public interface EthereumRpc {
    String web3_clientVersion();
    
    NodeInfo admin_nodeInfo();
    
    boolean admin_addPeer(String enode);
    
    List<String> eth_accounts();
    
    Address eth_coinbase();
    
    boolean eth_mining();
    
    boolean miner_start(int threads);
    
    Address personal_newAccount(String password);
    
    boolean personal_unlockAccount(Address address, String password);
    
    boolean personal_unlockAccount(Address address, String password, int durationSeconds);
    
    String eth_getBalance(Address account, String when);
    
    boolean miner_stop();
    
    CompilationResults eth_compileSolidity(String source);

	String eth_sendTransaction(Transaction transaction);
	
	BlockInfo eth_getBlockByHash(String hash, boolean fullTransactionInfo);
	
	List<Map<String, Object>> eth_getLogs(Filter filter);
	
	String eth_newFilter(Filter filter);

	String eth_newBlockFilter();
	
	String eth_newPendingTransactionFilter();

	// Could be String or LinkedHashMap depending on type of filter
	List<Object> eth_getFilterChanges(String filterId);
	
	boolean eth_uninstallFilter(String filterId);
	
	// Could be String or LinkedHashMap depending on type of filter
	List<Object> eth_getFilterLogs(String filterId);

	TransactionReceipt eth_getTransactionReceipt(String transactionHash);
	
	String eth_call(Transaction transaction, String defaultBlock);

	String eth_getCode(Address contractAddress, String defaultBlock);

	List<NodeInfo> admin_peers();

	Object eth_syncing();

	String eth_getTransactionCount(Address account, String defaultBlock);
}