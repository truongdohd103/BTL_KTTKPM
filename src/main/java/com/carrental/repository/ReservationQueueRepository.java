package com.carrental.repository;

import com.carrental.model.ReservationQueue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationQueueRepository extends JpaRepository<ReservationQueue, Long> {
}
