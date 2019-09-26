pragma solidity ^0.4.1;

contract Owned {

  address owner;

  function owned() {
      owner = msg.sender;
  }

  modifier onlyOwner {
    if (msg.sender != owner) {
      throw;
    }
    _;
  }

}
