# json-rpc-core

This package contains code that allows you to convert a java interface in to [JSON-RPC 2.0](http://www.jsonrpc.org/specification) messages and vice versa. It makes it simple to make RPC calls over a given [JsonRpcTransport](src/main/java/com/std/ie/jsonrpc/JsonRpcTransport.java). This package includes an in-memory version as a demonstration (The in-memory version just provides an easy testing mechanism. It is envisages that most uses will be over some kind of messaging layer).

## Usage

Start by creating an [InMemoryJsonRpcTransport](src/main/java/com/std/ie/jsonrpc/InMemoryJsonRpcTransport.java) and a [JsonRpcFactory](src/main/java/com/std/ie/jsonrpc/JsonRpcFactory.java):

```java
		final JsonRpcTransport transport = new InMemoryJsonRpcTransport("A unique address");

		final JsonRpcFactory jsonRpcFactory = new JsonRpcFactory(transport);
```

Register a service with the factory. In this case we register a simple *BiFunction* that takes two integer arguments and returns the sum of them:

```java
		jsonRpcFactory.registerService(BiFunction.class, new BiFunction<Integer, Integer, Integer>() {
			@Override
			public Integer apply(Integer t, Integer u) {
				return Integer.valueOf(t.intValue() + u.intValue());
			};
		});
```

Against the same factory we create a client to call the service (in real use cases this would be in a different VM):

```java	
		final BiFunction<Integer, Integer, Integer> add = jsonRpcFactory.newClient(BiFunction.class, 10, TimeUnit.SECONDS);		
```

And finally call the function:

```java
		add.apply(1, 2)); // returns 3 :o)
```

Although rudimental, this example serves to demonstrate the functionality.

Another feature present in the library is the ability to register callback types. This allows for JSON-RPC calls to be initiated from the server side:

```java
		jsonRpcFactory.registerCallbackType(Function.class);
```

This changes the default behaviour of serializing all arguments using [Jackson Databind](https://github.com/FasterXML/jackson-databind). Instead the client registers a service using a derived transport (e.g. when using a pub / sub messaging system this would create a new channel name to send messages on). It then sends an address identifier that can be used to create a client on the server side. Examples can be seen in the [InMemoryIntegrationTest](src/test/java/com/std/ie/jsonrpc/InMemoryIntegrationTest.java).

**N.B.**: You must register callback types on both the server and the client side when using a distributed transport.
