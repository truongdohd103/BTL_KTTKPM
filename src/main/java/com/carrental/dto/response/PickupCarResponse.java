package com.carrental.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.carrental.dto.common.CarDto;
import com.carrental.dto.common.CustomerDto;
import com.carrental.dto.common.EmployeeDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PickupCarResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("discount_code")
    private String discountCode;

    @JsonProperty("return_date")
    private LocalDate returnDate;

    @JsonProperty("car_condition")
    private String carCondition;

    @JsonProperty("additional_fees")
    private BigDecimal additionalFees;

    @JsonProperty("car")
    private CarDto car;

    @JsonProperty("customer")
    private CustomerDto customer;

    @JsonProperty("employee")
    private EmployeeDto employee;
}