# json-rpc-rabbitmq

A [RabbitMQ](https://www.rabbitmq.com) based transport. JSON-RPC messages are sent over two queues, one for server in-bound messages and one for server out-bound messages. Derived transports for callback's take the base transport name and append a unqiue id to it.

## Usage

Create a [RabbitMQJsonRpcTransport](src/main/java/com/std/ie/jsonrpc/rabbitmq/RabbitMQJsonRpcTransport.java) by passing in a *ConnectionFactory*:

```java
		final ConnectionFactory connectionFactory = new ConnectionFactory();

		connectionFactory.setHost(host);
		connectionFactory.setPort(port);
		
		transport = new RabbitMQJsonRpcTransport(connectionFactory, "demo");
```

This will send JSON-RPC messages over the [default exchange](https://www.rabbitmq.com/tutorials/tutorial-three-python.html] in RabbitMQ to the routing keys *demo/in* and *demo/out*. Any callback objects that are registered and then serialized will result in routing keys of the form *demo/n/in* and *demo/n/out* where n is incremented from zero upwards. 