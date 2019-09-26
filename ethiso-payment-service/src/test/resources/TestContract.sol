pragma solidity ^0.4.0;

import "Base.sol";

contract TestContract is Base {

	function TestContract(int a) {
		Construct(a);
	}
	
	event Construct(
		int val
	);
}