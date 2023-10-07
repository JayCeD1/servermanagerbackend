package com.erenes.domain.repositories;

import com.erenes.domain.Server;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServerRepo implements PanacheRepository<Server> {

    public Uni<Server> findByIpAddress(String ipAddress){
        return find("ipAddress",ipAddress)
                .firstResult();
    }
}
