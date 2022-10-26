# jsonblocaldatereproducer Project

For reproducing a problem with Quarkus, Reactive Hibernate, Panache, Reactive SQL Client

Created Project via https://code.quarkus.io/

Followed the guides at https://quarkus.io/guides/hibernate-reactive-panache and https://quarkus.io/guides/reactive-sql-clients (but using the Panache approach, not the reactive clients, i.e. no named query for inserts).

Final _pom.xml_ (NB Lombok added):

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

DB manually started (as dev services DB instantiation didn't work) w/

```podman run -it --rm=true --name exampledb -e POSTGRES_USER=exampledb -e POSTGRES_PASSWORD=exampledb -e POSTGRES_DB=exampledb -p 5432:5432 postgres:14.5```



