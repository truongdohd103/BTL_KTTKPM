package com.carrental.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "reservation_queue")
public class ReservationQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_id")
    @JsonProperty("car")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonProperty("customer")
    private Customer customer;

    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("expire_date")
    private LocalDate expireDate;

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("request_date")
    private LocalDate requestDate;
}