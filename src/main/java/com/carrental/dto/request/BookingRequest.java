package com.carrental.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @JsonProperty("car_id")
    private Long carId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("employee_id")
    private Long employeeId;

    @JsonProperty("start_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @JsonProperty("end_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @JsonProperty("status")
    private String status;

    // Bỏ trường totalAmount vì sẽ tính ở backend
    // @JsonProperty("total_amount")
    // private BigDecimal totalAmount;

    @JsonProperty("discount_code")
    private String discountCode;
}