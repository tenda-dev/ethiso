pragma solidity ^0.4.1;

import "./Owned.sol";

contract Mortal is Owned {

    function close() onlyOwner {
        suicide(owner);
    }

}
