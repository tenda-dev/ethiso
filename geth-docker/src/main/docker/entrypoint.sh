#!/bin/bash

COMMAND=$1

shift 1

if [ "$COMMAND" == "geth" -o "$COMMAND" == "geth-test" ]; then
  IP_ADDRESS=$(ifconfig eth0 | grep "inet " | awk '{ print $2 }' | awk -F":" '{ print $2 }')

  echo starting geth on ip address ${IP_ADDRESS}

  if [ "$TEST_ACCOUNT" ]; then
  	TEST_ACCOUNT_FOLDER=test-accounts/$TEST_ACCOUNT
  	
  	if [ -d "$TEST_ACCOUNT_FOLDER" ]; then
	  echo "Copying accounts from [$TEST_ACCOUNT_FOLDER]" 
	  mkdir .ethereum/keystore
	  cp $TEST_ACCOUNT_FOLDER/* .ethereum/keystore
	  chmod -R 600 .ethereum/keystore
	else
	  echo "Test account folder [$TEST_ACCOUNT_FOLDER] not found" 
	fi
  fi
  
  exec geth --nat extip:$IP_ADDRESS $*
elif [ "$COMMAND" == "solc" ]; then
  umask 000

  solc $*
  
  echo "solc terminated with code $?"
else
  echo Unknown command "$COMMAND". Expected "geth", "geth-test" or "solc".
fi
