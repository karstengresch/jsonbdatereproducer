package org.bydoing;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/example")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ExampleResource {

    @Inject
    io.vertx.mutiny.pgclient.PgPool pgPoolForClient;

    @Inject
    @ConfigProperty(name = "exampledb.schema.create", defaultValue = "true")
    boolean createDbSchema;


    void config(@Observes StartupEvent startupEvent) {
        System.out.println("********** Startup Event!");
        if (createDbSchema) {
            initdb();
        }
    }

    private void initdb() {
        pgPoolForClient.query("DROP TABLE IF EXISTS exampletable").execute()
          .flatMap(r -> pgPoolForClient.query("CREATE TABLE exampletable (id SERIAL PRIMARY KEY, exampledate TIMESTAMP WITH TIME ZONE NOT NULL)").execute())
          .flatMap(r -> pgPoolForClient.query("INSERT INTO exampletable (exampledate) VALUES ('2022-08-24T14:00')").execute())
          .flatMap(r -> pgPoolForClient.query("INSERT INTO exampletable (exampledate) VALUES ('2022-09-25T15:00')").execute())
          .flatMap(r -> pgPoolForClient.query("INSERT INTO exampletable (exampledate) VALUES ('2022-10-26T16:00')").execute())
          .await().indefinitely();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<ExampleEntity>> getAll() {
        return ExampleEntity.getAllExampleEntities();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> createExample(ExampleEntity example) {
        Log.debug("ExampleResource.createExample with: " + example.toString());
        return ExampleEntity.createExampleEntity(example)
          .onItem().transform(id -> URI.create("/v1/example/" + id.id))
          .onItem().transform(uri -> Response.created(uri))
          .onItem().transform(Response.ResponseBuilder::build);
    }

}