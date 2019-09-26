FROM node:5.11-onbuild


ENV RPC_PORT=8545
ENV RPC_HOST=geth-docker-argent
ENV WS_SERVER=http://ethstats-argent:3000
ENV WS_SECRET=admin
ENV INSTANCE_NAME=geth-docker-argent

ENTRYPOINT ["node","app.js"]

