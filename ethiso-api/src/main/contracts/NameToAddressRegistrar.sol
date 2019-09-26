pragma solidity ^0.4.1;

contract NameToAddressRegistrar {

	function() {
		// prevent people from just sending funds to the registrar
		throw;
	}

	function setAddress(string _name, address _a) {
		addresses[_name] = _a;
	}

	function getAddress(string _name) constant returns (address) { return addresses[_name]; }

	mapping (string => address) addresses;
}