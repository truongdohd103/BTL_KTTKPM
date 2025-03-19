package com.carrental.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "discount")
@Data
public class Discount {
    @Id
    @JsonProperty("code")
    private String code;

    @JsonProperty("value")
    private BigDecimal value;

    @Column(name = "expiry_date")
    @JsonProperty("expiry_date")
    private LocalDate expiryDate;
}