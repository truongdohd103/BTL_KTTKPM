package com.carrental.service;

import com.carrental.exception.CarNotFoundException;
import com.carrental.model.Car;
import com.carrental.model.RentalStore;
import com.carrental.model.VehicleCategory;
import com.carrental.repository.CarRepository;
import com.carrental.repository.RentalStoreRepository;
import com.carrental.repository.VehicleCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final CarRepository carRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;
    private final RentalStoreRepository rentalStoreRepository;

    @Autowired
    public AdminService(CarRepository carRepository, VehicleCategoryRepository vehicleCategoryRepository, RentalStoreRepository rentalStoreRepository) {
        this.carRepository = carRepository;
        this.vehicleCategoryRepository = vehicleCategoryRepository;
        this.rentalStoreRepository = rentalStoreRepository;
    }

    public Car addCar(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }
        if (car.getCategory() == null || car.getRentalStore() == null || car.getLicensePlate() == null || car.getPricePerDay() == null) {
            throw new IllegalArgumentException("Required fields (category, rentalStore, licensePlate, pricePerDay) cannot be null");
        }
        // Kiểm tra VehicleCategory và RentalStore tồn tại
        VehicleCategory category = vehicleCategoryRepository.findById(car.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle category not found with ID: " + car.getCategory().getId()));
        RentalStore rentalStore = rentalStoreRepository.findById(car.getRentalStore().getId())
                .orElseThrow(() -> new IllegalArgumentException("Rental store not found with ID: " + car.getRentalStore().getId()));
        car.setCategory(category);
        car.setRentalStore(rentalStore);

        if (car.getStatus() == null) {
            car.setStatus("AVAILABLE");
        }
        return carRepository.save(car);
    }

    public void updateCarStatus(Long carId, String status) {
        if (carId == null) {
            throw new IllegalArgumentException("Car ID cannot be null");
        }
        if (status == null || (!status.equals("AVAILABLE") && !status.equals("RENTED"))) {
            throw new IllegalArgumentException("Status must be either AVAILABLE or RENTED");
        }
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found with ID: " + carId));
        car.setStatus(status);
        carRepository.save(car);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }
}