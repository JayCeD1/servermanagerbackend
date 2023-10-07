package com.erenes.service;

import com.erenes.domain.Server;
import com.erenes.domain.repositories.ServerRepo;
import com.erenes.enumeration.Status;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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

    public Uni<Server> create(Server server){
        return Panache.withTransaction(() -> {
            server.imageUrl = "";

            return serverRepo.persist(server);
        });
    }

    public Uni<Server> ping(String ipAddress) {
        Uni<Server> serverUni = serverRepo.findByIpAddress(ipAddress);

        Uni<InetAddress> address = Uni.createFrom().item(Unchecked.supplier(() -> {
            try {
                return InetAddress.getByName(ipAddress);
            } catch (Exception e) {
                throw new WebApplicationException(e);
            }
        }));

        return Panache.withTransaction(() -> Uni.combine().all().unis(serverUni, address)
                .asTuple()
                .onItem().transform(Unchecked.function(tuple -> {
                    Server server = tuple.getItem1();
                    InetAddress inetAddress = tuple.getItem2();
                    try {
                        server.status = inetAddress.isReachable(10000) ? Status.SERVER_UP : Status.SERVER_DOWN;
                    } catch (IOException e) {
                        throw new IOException(e);
                    }
                    return server;
                }))
                .onFailure(IOException.class::isInstance)
                .recoverWithUni(() -> serverUni.onItem().transform(server -> {
                    server.status = Status.SERVER_DOWN;
                    return server;
                }))
                .onItem()
                .transformToUni(server -> serverRepo.persist(server))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
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
