package com.carrental.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonProperty("id")
    private Long id;

    @Column(name = "booking_id")
    @JsonProperty("booking_id")
    private Long bookingId;

    @Column(name = "amount")
    @JsonProperty("amount")
    private BigDecimal amount;

    @Column(name = "method")
    @Enumerated(EnumType.STRING)
    @JsonProperty("method")
    private PaymentMethod method;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @JsonProperty("status")
    private PaymentStatus status;

    @Column(name = "payment_date")
    @JsonProperty("payment_date")
    private LocalDate paymentDate;

    public enum PaymentMethod {
        CREDIT_CARD,
        CASH;

        @JsonCreator
        public static PaymentMethod fromValue(String value) {
            System.out.println("Attempting to deserialize method: " + value); // Debug
            if (value == null) {
                System.out.println("Method is null");
                throw new IllegalArgumentException("Payment method cannot be null");
            }
            try {
                String normalizedValue = value.toUpperCase(); // Chỉ chuyển thành chữ hoa, không loại bỏ "_"
                System.out.println("Normalized method value: " + normalizedValue); // Debug
                PaymentMethod[] values = PaymentMethod.values(); // Debug all values
                for (PaymentMethod pm : values) {
                    System.out.println("Available enum value: " + pm.name());
                }
                PaymentMethod result = valueOf(normalizedValue);
                System.out.println("Successfully mapped method to: " + result); // Debug
                return result;
            } catch (IllegalArgumentException e) {
                System.err.println("Method deserialization failed for " + value + ": " + e.getMessage());
                throw new IllegalArgumentException("Unknown method: " + value + ". Accepted values are " + java.util.Arrays.toString(values()));
            }
        }

        @JsonValue
        public String toValue() {
            return name();
        }
    }

    public enum PaymentStatus {
        COMPLETED,
        PENDING;

        @JsonCreator
        public static PaymentStatus fromValue(String value) {
            System.out.println("Attempting to deserialize status: " + value);
            if (value == null) {
                System.out.println("Status is null");
                throw new IllegalArgumentException("Payment status cannot be null");
            }
            try {
                String normalizedValue = value.toUpperCase();
                System.out.println("Normalized status value: " + normalizedValue);
                PaymentStatus result = valueOf(normalizedValue);
                System.out.println("Successfully mapped status to: " + result);
                return result;
            } catch (IllegalArgumentException e) {
                System.err.println("Status deserialization failed for " + value + ": " + e.getMessage());
                throw new IllegalArgumentException("Unknown status: " + value + ". Accepted values are [COMPLETED, PENDING]");
            }
        }

        @JsonValue
        public String toValue() {
            return name();
        }
    }
}