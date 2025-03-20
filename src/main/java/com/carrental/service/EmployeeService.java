package com.carrental.service;

import com.carrental.dto.common.EmployeeDto;
import com.carrental.model.Employee;
import com.carrental.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    public Employee getEmployeeEntityById(Long employeeId) {
        logger.debug("Fetching employee entity with ID: {}", employeeId);
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));
    }

    public EmployeeDto getEmployeeById(Long employeeId) {
        logger.debug("Fetching employee with ID: {}", employeeId);
        Employee employee = getEmployeeEntityById(employeeId);
        return modelMapper.map(employee, EmployeeDto.class);
    }
}