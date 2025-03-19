package com.carrental.dto.request;

import com.carrental.model.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentRequest {
    @JsonProperty("booking_id")
    private Long bookingId;

    @JsonProperty("payment_date")
    private LocalDate paymentDate;

    @JsonProperty("method")
    private Payment.PaymentMethod method;

    @JsonProperty("status")
    private Payment.PaymentStatus status;
}