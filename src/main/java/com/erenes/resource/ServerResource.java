package com.erenes.resource;

import com.erenes.domain.Server;
import com.erenes.generals.HttpResponse;
import com.erenes.service.ServerService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;

import static com.erenes.enumeration.Status.SERVER_UP;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("server")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerResource {

    @Inject
    ServerService service;

    @GET
    @Path("list")
    public Uni<Response> getServers(){
        return service.list(30)
                .onItem()
                .transform(servers -> Response.ok().entity(HttpResponse.newBuilder()
                        .message("Servers retrieved")
                        .data(Map.of("servers",servers))
                        .timeStamp(Instant.now().toString())
                        .status(Status.OK)
                        .statusCode(Status.OK.getStatusCode())
                        .build()
                ).build());
    }

    @GET
    @Path("ping/{ipAddress}")
    public Uni<Response> pingServer(@PathParam("ipAddress") String ipAddress){
        return service.ping(ipAddress)
                .onItem()
                .transform(server -> Response.ok().entity(HttpResponse.newBuilder()
                        .message(server.status == SERVER_UP ? "Ping Success" : "Ping Failed")
                        .data(Map.of("server",server))
                        .timeStamp(Instant.now().toString())
                        .status(Status.OK)
                        .statusCode(Status.OK.getStatusCode())
                        .build()
                ).build())
                .onFailure()
                .invoke(failure -> {System.err.println(failure);})
                .onFailure()
                .recoverWithItem(() -> Response.status(BAD_REQUEST).entity(HttpResponse.newBuilder()
                        .timeStamp(Instant.now().toString())
                        .message("Error Occurred")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.getStatusCode())
                        .build()).build());
    }

    @POST
    @Path("save")
    public Uni<Response> save(@Valid Server server){
        return service.create(server)
                .onItem()
                .transform(serve -> Response.ok().entity(HttpResponse.newBuilder()
                        .message("Server created")
                        .data(Map.of("server",serve))
                        .timeStamp(Instant.now().toString())
                        .status(CREATED)
                        .statusCode(CREATED.getStatusCode())
                        .build()
                ).build())
                .onFailure()
                .recoverWithItem(() -> Response.status(BAD_REQUEST).entity(HttpResponse.newBuilder()
                        .timeStamp(Instant.now().toString())
                        .message("Error Occurred")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.getStatusCode())
                        .build()).build());
    }

    @GET
    @Path("get/{id}")
    public Uni<Response> getServer(@PathParam("id") Long id){
        return service.get(id)
                .onItem()
                .transform(server -> Response.ok().entity(HttpResponse.newBuilder()
                        .message("Server retrieved")
                        .data(Map.of("server",server))
                        .timeStamp(Instant.now().toString())
                        .status(Status.OK)
                        .statusCode(Status.OK.getStatusCode())
                        .build()
                ).build())
                .onFailure()
                .recoverWithItem(() -> Response.status(BAD_REQUEST).entity(HttpResponse.newBuilder()
                        .timeStamp(Instant.now().toString())
                        .message("Error Occurred")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.getStatusCode())
                        .build()).build());
    }

    @DELETE
    @Path("delete/{id}")
    public Uni<Response> deleteServer(@PathParam("id") Long id){
        return service.delete(id)
                .onItem()
                .transform(deleted -> Response.ok().entity(HttpResponse.newBuilder()
                        .message("Server deleted")
                        .data(Map.of("deleted",deleted))
                        .timeStamp(Instant.now().toString())
                        .status(Status.OK)
                        .statusCode(Status.OK.getStatusCode())
                        .build()
                ).build())
                .onFailure()
                .recoverWithItem(() -> Response.status(BAD_REQUEST).entity(HttpResponse.newBuilder()
                        .timeStamp(Instant.now().toString())
                        .message("Error Occurred")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.getStatusCode())
                        .build()).build());
    }

    @GET
    @Path("image/{fileName}")
    public byte[] getServerImage(@PathParam("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/Downloads/images/" + fileName));
    }
}
