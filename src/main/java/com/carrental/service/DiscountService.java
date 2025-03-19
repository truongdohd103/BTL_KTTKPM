package com.carrental.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DiscountService {
    public BigDecimal calculateDiscount(String discountCode, BigDecimal totalAmount) {
        if ("DISCOUNT10".equals(discountCode)) {
            return totalAmount.multiply(new BigDecimal("0.10")); // Giảm 10%
        }
        return BigDecimal.ZERO;
    }
}