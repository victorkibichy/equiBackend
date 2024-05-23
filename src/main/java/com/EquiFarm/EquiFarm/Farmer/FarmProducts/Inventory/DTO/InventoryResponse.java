package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Inventory.DTO;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.FarmProductsResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    private Long id;
//    private FarmProductsResponse farmProducts;
    private Double unitsAvailable;
    private Double unitsReserved;
    private Double unitsSold;
}
