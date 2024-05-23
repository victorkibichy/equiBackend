package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.Inventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgriInventoryRequest {
    private Long agriProductId;
    private Double unitsAvailable;
    private Double unitsReserved;
}
