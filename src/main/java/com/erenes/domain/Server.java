package com.erenes.domain;

import com.erenes.enumeration.Status;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Server extends PanacheEntity {

    @Column(unique = true)
    @NotEmpty(message = "IP Address cannot be empty or null")
    public String ipAddress;

    @Column
    public String name;

    @Column
    public String memory;

    @Column
    public String type;

    @Column
    public String imageUrl;

    @Column
    public Status status;
}
