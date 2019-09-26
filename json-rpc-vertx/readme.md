# json-rpc-vertx

A [Vert.x](http://vertx.io) based transport. JSON-RPC messages are sent over the event bus, with the address set to the base transport address, and responded to directly using the *reply()* method. Derived transports for callback's take the base transport name and append a unqiue id to it.

## Usage

Create an [EventBusJsonRpcTransport](src/main/java/com/std/ie/jsonrpc/vertx/EventBusJsonRpcTransport.java) by passing in a *Vertx* instance and an address:

```java
		final Vertx vertx = Vertx.vertx();
				
		transport = new EventBusJsonRpcTransport(vertx, "demo");
```

This will send JSON-RPC messages over the Event Bus in Vert.x to the address *demo*. Responses are sent directly back to the sender using *reply()*. Any callback objects that are registered and then serialized will result in addresses of the form *demo/n* n is incremented from zero upwards.