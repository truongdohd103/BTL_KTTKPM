package com.carrental.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "booking")
@Data // Sử dụng Lombok để tự động tạo getter/setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    @JsonProperty("car")
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonProperty("customer")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @JsonProperty("employee")
    private Employee employee;

    @Column(name = "start_date")
    @JsonProperty("start_date")
    private LocalDate start_date;

    @Column(name = "end_date")
    @JsonProperty("end_date")
    private LocalDate end_date;

    @Column(name = "return_date")
    @JsonProperty("return_date")
    private LocalDate return_date;

    @Column(name = "car_condition")
    @JsonProperty("car_condition")
    private String car_condition;

    @Column(name = "additional_fees")
    @JsonProperty("additional_fees")
    private BigDecimal additional_fees;

    @Column(name = "status")
    @JsonProperty("status")
    private String status;

    @Column(name = "total_amount")
    @JsonProperty("total_amount")
    private BigDecimal total_amount;

    @Column(name = "discount_code")
    @JsonProperty("discount_code")
    private String discount_code;
}