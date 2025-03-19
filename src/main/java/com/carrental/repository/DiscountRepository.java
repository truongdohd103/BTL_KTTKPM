package com.carrental.repository;

import com.carrental.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, String> {
    Optional<Discount> findByCode(String code);
}