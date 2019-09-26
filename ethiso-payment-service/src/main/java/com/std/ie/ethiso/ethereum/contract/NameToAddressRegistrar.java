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
package com.std.ie.ethiso.ethereum.contract;

import com.std.ie.ethiso.ethereum.ContractProxy;
import com.std.ie.ethiso.ethereum.Ethereum;
import com.std.ie.ethiso.ethereum.rpc.Address;

import java.util.concurrent.ExecutionException;

public class NameToAddressRegistrar extends ContractProxy {

    public NameToAddressRegistrar(Ethereum ethereum, Mode mode, Address contractAddress) {
        super(ethereum, mode, contractAddress, "NameToAddressRegistrar");
    }

    public Address getAddress(final String name) {
        final Object[] result = call("getAddress", name);

        if (!(result[0] instanceof Address)) {
            throw new IllegalArgumentException("Invalid return type " + result[0].getClass());
        }

        return (Address) result[0];
    }

    public void setAddress(final String name, final Address address) {
        try {
            sendTransaction("setAddress", name, address).toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

}
