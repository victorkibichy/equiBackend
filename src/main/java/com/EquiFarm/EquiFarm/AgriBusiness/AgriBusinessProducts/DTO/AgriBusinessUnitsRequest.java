package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessUnitsRequest {
    private Double agriBusinessUnits;
    private Long agriBusinessProductId;
}
