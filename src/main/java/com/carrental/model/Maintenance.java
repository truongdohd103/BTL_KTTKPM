package com.carrental.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "maintenance")
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_id")
    @JsonProperty("car")
    private Car car;

    @JsonProperty("description")
    private String description;

    @JsonProperty("schedule_date")
    private LocalDate scheduleDate;

    @JsonProperty("completion_date")
    private LocalDate completionDate;

    @JsonProperty("cost")
    private BigDecimal cost;
}