package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Inventory.DTO;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.FarmProductsResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequest {
    private Long farmProductId;
    private Double unitsAvailable;
    private Double unitsReserved;
}
