package com.carrental.repository;

import com.carrental.model.RentalStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalStoreRepository extends JpaRepository<RentalStore, Long> {
}