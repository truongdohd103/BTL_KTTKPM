package com.carrental.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.carrental.dto.common.CarDto;
import com.carrental.dto.common.CustomerDto;
import com.carrental.dto.common.EmployeeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnCarResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("car")
    private CarDto car;

    @JsonProperty("customer")
    private CustomerDto customer;

    @JsonProperty("employee")
    private EmployeeDto employee;

    @JsonProperty("start_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @JsonProperty("end_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @JsonProperty("return_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate returnDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("discount_code")
    private String discountCode;

    @JsonProperty("car_condition")
    private String carCondition;

    @JsonProperty("additional_fees")
    private BigDecimal additionalFees;
}