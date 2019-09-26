FROM ubuntu:xenial

WORKDIR /root/

RUN set -x \
  && apt-get -qq update \
  && apt-get -qq install -y sudo software-properties-common lsb-release bzip2 libboost-filesystem-dev libboost-program-options-dev libjsoncpp-dev net-tools wget zip git build-essential libgmp3-dev golang golang-golang-x-crypto-dev golang-github-azure-azure-sdk-for-go-dev \
  && rm -rf /var/lib/apt/lists/*

RUN git clone --recursive https://github.com/ethereum/solidity.git && cd solidity && git reset --hard v0.4.6
RUN ./solidity/scripts/install_deps.sh
RUN mkdir solidity/build && cd solidity/build && cmake .. && make
RUN mv solidity/build/solc/solc /usr/bin

RUN cd ../..

RUN git clone https://github.com/ethereum/go-ethereum

RUN (cd go-ethereum && git reset --hard v1.5.4)
ADD api.go go-ethereum/node/
ADD tx_pool.go go-ethereum/core/
RUN (cd go-ethereum && make geth)

RUN mv go-ethereum/build/bin/geth /usr/bin




RUN mkdir ~/.ethash

COPY CustomGenesis.json CustomGenesis.json

COPY entrypoint.sh entrypoint.sh

COPY test-accounts/ test-accounts/

RUN chmod u+x entrypoint.sh

RUN geth init ./CustomGenesis.json

EXPOSE 8545

EXPOSE 30303

ENV TERM=xterm-256color

ENTRYPOINT ["./entrypoint.sh"]

CMD ["geth", "--networkid", "31415926", "--rpc", "--rpcaddr", "0.0.0.0", "--rpcapi", "eth,net,web3,admin,miner,personal", "--metrics", "--bootnodes", "\"\""]
