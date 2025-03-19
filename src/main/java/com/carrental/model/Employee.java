package com.carrental.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.persistence.*;

@Entity
@Table(name = "employee")
@Data
public class Employee {
    @Id
    @JsonProperty("id")
    private Long id;

    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Column(name = "role")
    @JsonProperty("role")
    private String role;
}