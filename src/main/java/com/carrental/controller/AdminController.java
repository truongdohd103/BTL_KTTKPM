package com.carrental.controller;

import com.carrental.model.Car;
import com.carrental.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/cars")
    public ResponseEntity<Car> addCar(@RequestBody Car car) {
        Car savedCar = adminService.addCar(car);
        return ResponseEntity.ok(savedCar);
    }

    @PutMapping("/cars/{carId}/status")
    public ResponseEntity<String> updateCarStatus(@PathVariable Long carId, @RequestParam String status) {
        adminService.updateCarStatus(carId, status);
        return ResponseEntity.ok("Car status updated successfully");
    }

    @GetMapping("/cars")
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = adminService.getAllCars();
        return ResponseEntity.ok(cars);
    }
}