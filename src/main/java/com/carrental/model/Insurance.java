package com.carrental.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "insurance")
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @JsonProperty("provider")
    private String provider;

    @JsonProperty("premium")
    private BigDecimal premium;

    @JsonProperty("coverage_date")
    private LocalDate coverageDate;

    @OneToOne
    @JoinColumn(name = "car_id")
    @JsonProperty("car")
    private Car car;
}