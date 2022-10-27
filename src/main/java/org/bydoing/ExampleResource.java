package org.bydoing;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
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

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<ExampleEntity>> getAll() {
        return ExampleEntity.getAllExampleEntities();
    }

    /**
     *
     * Valid Call:
     * <pre>
     * curl -X 'POST' \
     *   'http://localhost:8080/example' \
     *   -H 'accept: application/json' \
     *   -H 'Content-Type: application/json' \
     *   -d '{
     *   "id": 0,
     *   "exampledate": "2022-03-10T12:15:50-04:00"
     * }'
     * </pre>
     * */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Uni<Response> createExample(ExampleEntity example) {
        Log.debug("ExampleResource.createExample with: " + example.toString());
        return ExampleEntity.createExampleEntity(example)
          .onItem().transform(id -> URI.create("/v1/example/" + id.id))
          .onItem().transform(uri -> Response.created(uri))
          .onItem().transform(Response.ResponseBuilder::build);
    }

}