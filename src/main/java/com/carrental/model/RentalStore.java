package com.carrental.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.persistence.*;

@Entity
@Table(name = "rentalStore")
@Data
public class RentalStore {
    @Id
    @JsonProperty("id")
    private Long id;

    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Column(name = "address")
    @JsonProperty("address")
    private String address;
}