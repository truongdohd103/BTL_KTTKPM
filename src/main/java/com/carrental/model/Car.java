package com.carrental.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "car")
@Data
public class Car {
    @Id
    @JsonProperty("id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoryId")
    @JsonProperty("category")
    private VehicleCategory category;

    @ManyToOne
    @JoinColumn(name = "rentalStoreId")
    @JsonProperty("rental_store")
    private RentalStore rentalStore;

    @Column(name = "license_plate")
    @JsonProperty("license_plate")
    private String licensePlate;

    @Column(name = "price_per_day")
    @JsonProperty("price_per_day")
    private BigDecimal pricePerDay;

    @Column(name = "status")
    @JsonProperty("status")
    private String status;

    @Column(name = "start_date_available")
    @JsonProperty("start_date_available")
    private LocalDate startDateAvailable;

    @Column(name = "end_date_available")
    @JsonProperty("end_date_available")
    private LocalDate endDateAvailable;
}