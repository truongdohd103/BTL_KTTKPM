package com.carrental.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnCarRequest {
    @JsonProperty("return_date")
    private LocalDate returnDate;

    @JsonProperty("car_condition")
    private String carCondition;

    @JsonProperty("additional_fees")
    private BigDecimal additionalFees;
}