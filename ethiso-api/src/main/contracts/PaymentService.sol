pragma solidity ^0.4.1;

import "./Mortal.sol";

contract PaymentService is Mortal {

	event TrustlineModified(
		address indexed p1,
		address indexed p2,
		string ccy,
		string trustType,
		int forwardLimit,
		int reverseLimit,
		int forwardAllow,
		int reverseAllow
	);

	struct AllowLimit { // aToB
    int allow; // b accepts owing this much to a
    int limit; // a limits b to owing them this much
  }

  struct TrustlineValue {
    string ccy; // currency
    int p2OwesP1Amount; // current balance between the two parties for this currency - has to be from someone's POV
    AllowLimit p2OwesP1; // represents p1 limiting p2 to owe them, and p2 limiting it's debt
    AllowLimit p1OwesP2; // represents p2 limiting p1 to owe them, and p1 limiting it's debt
  }


	// slightly tortuous mappging.
	// we need a way to get at the Trustline Value for a pair of parties no matter which party is executing
	// we can use the address comparison to always access this in a specific order
	mapping (address => mapping ( address => mapping (string => TrustlineValue))) trustlines;

  // allows us to understand whether we are p1 or not
  function senderIsP1Against(address them) private returns(bool iAmP1) {
		address us = msg.sender;
		if (us < them) {
			return true;
		} else {
			return false;
		}
	}

  // get the TrustlineValue no matter who is the caller
	function getTrustlineValue(address them, string ccy) private returns(TrustlineValue storage tlv){
		address us = msg.sender;

		if (us < them) {
			/*sendDebugMessage('us < them (fromIban is p1)');*/
			return trustlines[us][them][ccy];
		} else {
			/*sendDebugMessage('us > them (fromIban is p2)');*/
			return trustlines[them][us][ccy];
		}
	}

	// allows us to limit the amount they can owe us (ie they can send us payments)
	function setLimit(address them, string ccy, int amount) returns(bool succeeded) {
    if (amount < 0) {
			return false;
		}

		var tlv = getTrustlineValue(them, ccy);
		if (senderIsP1Against(them)) {
			if (tlv.p2OwesP1.limit != amount) {
			
				TrustlineModified(msg.sender, them, ccy, 'limit', amount, tlv.p1OwesP2.limit, tlv.p1OwesP2.allow,  tlv.p2OwesP1.allow);
				
				tlv.p2OwesP1.limit = amount;
				
				}
		} else {
			if (tlv.p1OwesP2.limit != amount) {
				TrustlineModified(msg.sender, them, ccy, 'limit', amount, tlv.p2OwesP1.limit, tlv.p2OwesP1.allow,  tlv.p1OwesP2.allow);
				
				tlv.p1OwesP2.limit = amount;
			}
		}

    return true;
  }

	// allows us to retrieve the current limit against another party
	function getLimit(address them, string ccy, bool reverse) constant returns(int amount) {
		var tlv = getTrustlineValue(them, ccy);

		if (senderIsP1Against(them) != reverse) {
			amount = tlv.p2OwesP1.limit;
		} else {
			amount = tlv.p1OwesP2.limit;
		}
	}

	// allow ourselves to owe them money (ie we can send them payments)
  function setAllow(address them, string ccy, int amount) returns(bool succeeded) {
		if (amount < 0) {
			return false;
		}

		var tlv = getTrustlineValue(them, ccy);

		if (senderIsP1Against(them)) {
			if (tlv.p1OwesP2.allow != amount) {
				TrustlineModified(msg.sender, them, ccy, 'allow', tlv.p2OwesP1.limit, tlv.p1OwesP2.limit, amount, tlv.p1OwesP2.allow);
				tlv.p1OwesP2.allow = amount;
			}
		} else {
			if (tlv.p2OwesP1.allow != amount) {
				TrustlineModified(them, msg.sender, ccy, 'allow', tlv.p1OwesP2.limit, tlv.p2OwesP1.limit, amount, tlv.p2OwesP1.allow);
				tlv.p2OwesP1.allow = amount;
			}
		}
		
    return true;
  }

	// allows us to retrieve the current allowed holding against another party
	function getAllow(address them, string ccy, bool reverse) constant returns(int amount) {
		var tlv = getTrustlineValue(them, ccy);

		if (senderIsP1Against(them) != reverse) {
			amount = tlv.p1OwesP2.allow;
		} else {
			amount = tlv.p2OwesP1.allow;
		}
	}

	event PaymentMessage(
		address indexed fromBank,
		address indexed toBank,
		string messageId,
		int amount,
		string ccy,
		string additionalProperties
	);

  	// errorTypes:
	//  0 - payment succeeded
    //  1 - need to pay a valid address
	//  2 - cannot pay ourselves
	//  3 - negative amount
	//  4 - payment would exceed limits
	event ResultMessage(
		address indexed fromBank,
		string messageId,
		string message,
		int errorType
	);

	// for the 1.1 test we are going to pass in addresses rather than join this contract up with the bic registrar contract
	// this leaves us open to a security issue - we will address this in v2

	// send a payment to them - effectively means we owe them something
	// ccy is the currency
	// amount is the integer amount (would be pence for GBP for example)
	// messageId is an identifier that we use to uniquely identify this payment from a system perspective
	// additionalProperties is a string that contains a json representation or information that the destination bank might need - e.g. the remittance info
  function sendPayment(address them, string ccy, int amount, string messageId, string additionalProperties) returns(bool succeeded) {

		if (them == address(0x0)) {
			ResultMessage(msg.sender, messageId, 'need to pay a valid address', 1);
			return false;
		}

		if (them == msg.sender) {
			ResultMessage(msg.sender, messageId, 'cannot pay ourselves', 2);
			return false;
		}

		if (amount < 0) {
			ResultMessage(msg.sender, messageId, 'payment amount must be positive', 3);
			return false;
		}

		var tlv = getTrustlineValue(them, ccy);
		int newAmount;

		if (senderIsP1Against(them)) {
			newAmount = tlv.p2OwesP1Amount - amount;
			if (-newAmount <= tlv.p1OwesP2.limit && -newAmount <= tlv.p1OwesP2.allow) {
				tlv.p2OwesP1Amount = newAmount;

				// send payment message - this is only acted upon at the "them" bank
				PaymentMessage(msg.sender, them, messageId, amount, ccy, additionalProperties);
				ResultMessage(msg.sender, messageId, 'payment successful', 0);
			} else {
				ResultMessage(msg.sender, messageId, 'payment would exceed limits', 4);
			}
		} else { // we're p2
			newAmount = tlv.p2OwesP1Amount + amount;
			if (newAmount <= tlv.p2OwesP1.limit && newAmount <= tlv.p2OwesP1.allow) {
				tlv.p2OwesP1Amount = newAmount;

				// send payment message - this is only acted upon at the "them" bank
				PaymentMessage(msg.sender, them, messageId, amount, ccy, additionalProperties);
				ResultMessage(msg.sender, messageId, 'payment successful', 0);
			} else {
				ResultMessage(msg.sender, messageId, 'payment would exceed limits', 4);
			}
		}

    return true;
  }

	// returns the amount owed to them by us
  function getAmountOwedTo(address them, string ccy) returns(int balance) {
		var tlv = getTrustlineValue(them, ccy);

		if (senderIsP1Against(them)) {
			return -1 * tlv.p2OwesP1Amount;
		} else {
			return tlv.p2OwesP1Amount;
		}
  }

	/////////////////////////////
	// some debugging help     //
	// probably should lift up //
	/////////////////////////////

	// used for debugging
	event DebugMessage(
		address indexed from,
		string message
	);

	// used for debugging
	event AmountMessage(
		address indexed from,
		string message,
		int amount
	);

	// used for debugging
	event AddressMessage(
		address indexed from,
		string message,
		address us,
		address them
	);

	// used for debugging
	function sendDebugMessage(string a) {
		DebugMessage(msg.sender, a);
	}

	// used for debuggin
	function sendAmountMessage(string message, int amount) {
		AmountMessage(msg.sender, message, amount);
	}

}
