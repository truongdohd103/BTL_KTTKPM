package com.carrental.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @JsonProperty("booking_id")
    private Long bookingId;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("issued_date")
    private LocalDate issuedDate;
}