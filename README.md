# jsonbdatereproducer Project

>>> **This is the working solution, thanks to https://github.com/quarkusio/quarkus/issues/28877 !** <<<

For reproducing a problem with Quarkus, Reactive Hibernate, Panache, Reactive SQL Client with JSON-B (de)serialization.

## Problem Description

1. Created Project via https://code.quarkus.io/

2. Followed the guides at https://quarkus.io/guides/hibernate-reactive-panache and https://quarkus.io/guides/reactive-sql-clients (but using the Panache approach, not the reactive clients, i.e. no named query for inserts).<br /><br />*Not* following the CRUD resource approach (https://quarkus.io/guides/rest-data-panache) due to missing pgsql support.

3. Final _pom.xml_ (NB Lombok added, see full file in repo!):<br /><br />
```
 <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy-reactive-jsonb</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy-reactive-jackson</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy-reactive</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-arc</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-hibernate-reactive-rest-data-panache</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-reactive-pg-client</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-smallrye-openapi</artifactId>
    </dependency>
```

4. DB manually started (as dev services DB instantiation didn't work) w/

```podman run -it --rm=true --name exampledb -e POSTGRES_USER=exampledb -e POSTGRES_PASSWORD=exampledb -e POSTGRES_DB=exampledb -p 5432:5432 postgres:14.5```

5. Run 

```
curl -X 'GET' \
  'http://localhost:8080/example' \
  -H 'accept: application/json'
```

to get

```
[
  {
    "exampledate": "2023-08-24T14:00:00",
    "id": 1
  },
  {
    "exampledate": "2023-09-25T15:00:00",
    "id": 2
  },
  {
    "exampledate": "2023-10-26T16:00:00",
    "id": 3
  }
]
```


6. Run 

```
curl -X 'POST' \
  'http://localhost:8080/example' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": 0,
  "exampledate": "2022-03-10T12:15:50"
}'
```

(omitting the "id" parameter is optional)

and the error is

```
2022-10-27 11:58:27,843 ERROR [org.jbo.res.rea.com.cor.AbstractResteasyReactiveContext] (vert.x-eventloop-thread-3) Request failed: javax.json.bind.JsonbException: Unable to deserialize property 'exampledate' because of: Error parsing class java.time.OffsetDateTime from value: 2022-03-10T12:15:50-04:00. Check your @JsonbDateFormat has all time units for class java.time.OffsetDateTime type, or consider using org.eclipse.yasson.YassonConfig#ZERO_TIME_PARSE_DEFAULTING.
        at org.eclipse.yasson.internal.serializer.AbstractContainerDeserializer.deserializeInternal(AbstractContainerDeserializer.java:100)
        at org.eclipse.yasson.internal.serializer.AbstractContainerDeserializer.deserialize(AbstractContainerDeserializer.java:64)
        at org.eclipse.yasson.internal.Unmarshaller.deserializeItem(Unmarshaller.java:62)
        at org.eclipse.yasson.internal.Unmarshaller.deserialize(Unmarshaller.java:51)
        at org.eclipse.yasson.internal.JsonBinding.deserialize(JsonBinding.java:59)
        at org.eclipse.yasson.internal.JsonBinding.fromJson(JsonBinding.java:99)
        at org.jboss.resteasy.reactive.server.jsonb.JsonbMessageBodyReader.doReadFrom(JsonbMessageBodyReader.java:57)
        at org.jboss.resteasy.reactive.server.jsonb.JsonbMessageBodyReader.readFrom(JsonbMessageBodyReader.java:39)
        at org.jboss.resteasy.reactive.server.handlers.RequestDeserializeHandler.readFrom(RequestDeserializeHandler.java:122)
        at org.jboss.resteasy.reactive.server.handlers.RequestDeserializeHandler.handle(RequestDeserializeHandler.java:81)
        at io.quarkus.resteasy.reactive.server.runtime.QuarkusResteasyReactiveRequestContext.invokeHandler(QuarkusResteasyReactiveRequestContext.java:108)
        at org.jboss.resteasy.reactive.common.core.AbstractResteasyReactiveContext.run(AbstractResteasyReactiveContext.java:145)
        at org.jboss.resteasy.reactive.server.vertx.VertxResteasyReactiveRequestContext$1$1.handle(VertxResteasyReactiveRequestContext.java:78)
        at org.jboss.resteasy.reactive.server.vertx.VertxResteasyReactiveRequestContext$1$1.handle(VertxResteasyReactiveRequestContext.java:75)
        at io.vertx.core.impl.ContextInternal.dispatch(ContextInternal.java:264)
        at io.vertx.core.impl.ContextInternal.dispatch(ContextInternal.java:246)
        at io.vertx.core.impl.EventLoopContext.lambda$runOnContext$0(EventLoopContext.java:43)
        at io.netty.util.concurrent.AbstractEventExecutor.runTask(AbstractEventExecutor.java:174)
        at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:167)
        at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:470)
        at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:566)
        at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:997)
        at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
        at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
        at java.base/java.lang.Thread.run(Thread.java:833)
Caused by: javax.json.bind.JsonbException: Error parsing class java.time.OffsetDateTime from value: 2022-03-10T12:15:50-04:00. Check your @JsonbDateFormat has all time units for class java.time.OffsetDateTime type, or consider using org.eclipse.yasson.YassonConfig#ZERO_TIME_PARSE_DEFAULTING.
        at org.eclipse.yasson.internal.serializer.AbstractDateTimeDeserializer.parseWithFormatterInternal(AbstractDateTimeDeserializer.java:135)
        at org.eclipse.yasson.internal.serializer.AbstractDateTimeDeserializer.deserialize(AbstractDateTimeDeserializer.java:59)
        at org.eclipse.yasson.internal.serializer.AbstractValueTypeDeserializer.deserialize(AbstractValueTypeDeserializer.java:64)
        at org.eclipse.yasson.internal.serializer.ObjectDeserializer.deserializeNext(ObjectDeserializer.java:183)
        at org.eclipse.yasson.internal.serializer.AbstractContainerDeserializer.deserializeInternal(AbstractContainerDeserializer.java:94)
        ... 24 more
Caused by: java.time.format.DateTimeParseException: Text '2022-03-10T12:15:50-04:00' could not be parsed, unparsed text found at index 19
        at java.base/java.time.format.DateTimeFormatter.parseResolved0(DateTimeFormatter.java:2055)
        at java.base/java.time.format.DateTimeFormatter.parse(DateTimeFormatter.java:1954)
        at java.base/java.time.OffsetDateTime.parse(OffsetDateTime.java:404)
        at org.eclipse.yasson.internal.serializer.OffsetDateTimeTypeDeserializer.parseWithFormatter(OffsetDateTimeTypeDeserializer.java:58)
        at org.eclipse.yasson.internal.serializer.OffsetDateTimeTypeDeserializer.parseWithFormatter(OffsetDateTimeTypeDeserializer.java:28)
        at org.eclipse.yasson.internal.serializer.AbstractDateTimeDeserializer.parseWithFormatterInternal(AbstractDateTimeDeserializer.java:133)
        ... 28 more
```

7. Checkout the localdatetime branch and run (5, optional, and) 6. again and you'll get this error

```
2022-10-27 12:03:26,491 ERROR [io.qua.ver.htt.run.QuarkusErrorHandler] (vert.x-eventloop-thread-3) HTTP Request to /example failed, error id: b6e2928a-f9de-40bc-9180-0920df35f1e0-1: java.lang.IllegalStateException: io.vertx.core.impl.NoStackTraceThrowable: Parameter at position[0] with class = [java.time.LocalDateTime] and value = [2022-03-10T12:15:50] can not be coerced to the expected class = [java.time.OffsetDateTime] for encoding.
        at org.bydoing.ExampleEntity.lambda$createExampleEntity$0(ExampleEntity.java:49)
        at io.smallrye.context.impl.wrappers.SlowContextualFunction.apply(SlowContextualFunction.java:21)
        at io.smallrye.mutiny.operators.uni.UniOnFailureTransform$UniOnFailureTransformProcessor.onFailure(UniOnFailureTransform.java:54)
        at io.smallrye.mutiny.operators.uni.UniFailOnTimeout$UniFailOnTimeoutProcessor.onFailure(UniFailOnTimeout.java:80)
        at io.smallrye.mutiny.operators.uni.UniOperatorProcessor.onFailure(UniOperatorProcessor.java:54)
        at io.smallrye.mutiny.operators.uni.UniOperatorProcessor.onFailure(UniOperatorProcessor.java:54)
        at io.smallrye.mutiny.operators.uni.UniOnItemOrFailureConsume$UniOnItemOrFailureConsumeProcessor.onFailure(UniOnItemOrFailureConsume.java:46)
        at io.smallrye.mutiny.operators.uni.UniOperatorProcessor.onFailure(UniOperatorProcessor.java:54)
        at io.smallrye.mutiny.operators.uni.UniOperatorProcessor.onFailure(UniOperatorProcessor.java:54)
        at io.smallrye.mutiny.operators.uni.UniOperatorProcessor.onFailure(UniOperatorProcessor.java:54)
        at io.smallrye.mutiny.operators.uni.UniOnCancellationCall$UniOnCancellationCallProcessor.onFailure(UniOnCancellationCall.java:59)
        at io.smallrye.mutiny.operators.uni.UniOnFailureFlatMap$UniOnFailureFlatMapProcessor.onFailure(UniOnFailureFlatMap.java:62)
        at io.smallrye.mutiny.operators.uni.UniOnFailureTransform$UniOnFailureTransformProcessor.onFailure(UniOnFailureTransform.java:64)
        at io.smallr
```

8. Checkout the nolombok branch if you wish to confirm that the problem exists w/o Lombok dependency and annotations.

## Expected Behavior
The POST method should be executed, a new record should have been inserted into the DB and the method should HTTP 200.


