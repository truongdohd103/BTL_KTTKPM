package com.carrental.controller;

import com.carrental.dto.common.CarDto;
import com.carrental.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private static final Logger logger = LoggerFactory.getLogger(CarController.class);

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/{carId}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Long carId) {
        logger.debug("Received request to get car with ID: {}", carId);
        try {
            CarDto carDto = carService.getCarById(carId);
            return ResponseEntity.ok(carDto);
        } catch (Exception e) {
            logger.error("Error fetching car: carId={}, error={}", carId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<CarDto>> getAllCars() {
        logger.debug("Received request to get all cars");
        try {
            List<CarDto> carDtos = carService.getAllCars();
            return ResponseEntity.ok(carDtos);
        } catch (Exception e) {
            logger.error("Error fetching all cars: error={}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}