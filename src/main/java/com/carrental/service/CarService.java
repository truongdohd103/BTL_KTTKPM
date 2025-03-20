package com.carrental.service;

import com.carrental.dto.common.CarDto;
import com.carrental.model.Car;
import com.carrental.repository.CarRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {

    private static final Logger logger = LoggerFactory.getLogger(CarService.class);

    private final CarRepository carRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CarService(CarRepository carRepository, ModelMapper modelMapper) {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
    }

    public CarDto getCarById(Long carId) {
        logger.debug("Fetching car with ID: {}", carId);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with ID: " + carId));
        return modelMapper.map(car, CarDto.class);
    }

    public Car getCarEntityById(Long carId) {
        logger.debug("Fetching car entity with ID: {}", carId);
        return carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with ID: " + carId));
    }

    public List<CarDto> getAllCars() {
        logger.debug("Fetching all cars");
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(car -> modelMapper.map(car, CarDto.class))
                .collect(Collectors.toList());
    }
}