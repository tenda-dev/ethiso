pragma solidity ^0.4.1;

contract Base {

	event Sum(
		int sum
	);
	
	function add(int a, int b) {
		Sum(a + b);
	}
}