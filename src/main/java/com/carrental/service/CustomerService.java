package com.carrental.service;

import com.carrental.dto.common.CustomerDto;
import com.carrental.model.Customer;
import com.carrental.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    public Customer getCustomerEntityById(Long customerId) {
        logger.debug("Fetching customer entity with ID: {}", customerId);
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
    }

    public CustomerDto getCustomerById(Long customerId) {
        logger.debug("Fetching customer with ID: {}", customerId);
        Customer customer = getCustomerEntityById(customerId);
        return modelMapper.map(customer, CustomerDto.class);
    }
}