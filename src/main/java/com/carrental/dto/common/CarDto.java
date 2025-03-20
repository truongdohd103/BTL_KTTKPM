package com.carrental.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("license_plate")
    private String licensePlate;

    @JsonProperty("category")
    private VehicleCategoryDto category;

    @JsonProperty("price_per_day") // Thêm trường pricePerDay
    private BigDecimal pricePerDay;
}