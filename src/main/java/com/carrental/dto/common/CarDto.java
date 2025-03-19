package com.carrental.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}