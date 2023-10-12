package com.erenes.service;

import com.erenes.domain.Server;
import com.erenes.domain.repositories.ServerRepo;
import com.erenes.enumeration.Status;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

@ApplicationScoped
public class ServerService {

    @Inject
    ServerRepo serverRepo;

    @Inject
    UriInfo uriInfo;

    @Inject
    Vertx vertx;

    public Uni<Server> create(Server server){
        return Panache.withTransaction(() -> {
            server.imageUrl = "";

            return serverRepo.persist(server);
        });
    }

    public Uni<Server> ping(String ipAddress) {
        Uni<Server> serverUni = serverRepo.findByIpAddress(ipAddress).onItem().ifNull().failWith(new NotFoundException());

        Uni<Boolean> address = Uni.createFrom().emitter(emitter -> {
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                boolean isReachable = inetAddress.isReachable(10000);
                emitter.complete(isReachable);
            } catch (Exception e) {
                emitter.fail(e);
            }
        });

        return Panache.withTransaction(() -> Uni.combine().all().unis(serverUni, address)
                .asTuple()
                .onItem().transformToUni(tuple -> {
                    Server server = tuple.getItem1();
                    server.status = Boolean.TRUE.equals(tuple.getItem2()) ? Status.SERVER_UP : Status.SERVER_DOWN;
                    return serverRepo.persist(server);
                })
                .onFailure(IOException.class::isInstance)
                .recoverWithUni(() -> serverUni.onItem().transform(server -> {
                    server.status = Status.SERVER_DOWN;
                    return server;
                }))
//                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
        );
    }

    public Uni<List<Server>> list(int limit){
        return serverRepo.findAll()
                .page(0, limit)
                .list();
    }

    public Uni<Server> get(Long id){
        return serverRepo.findById(id);
    }

    public Uni<Server> update(Server server){
        return Panache.withTransaction(() ->
            serverRepo.persist(server)
        );
    }

    public Uni<Boolean> delete(Long id){
        return serverRepo.deleteById(id);
    }

    private String getVerificationUrl(String key, String type){
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        return builder.path("user/verify/" + type + "/" + key).toString();
    }
}
