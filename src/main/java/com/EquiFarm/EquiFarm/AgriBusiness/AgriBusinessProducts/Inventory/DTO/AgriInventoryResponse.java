package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.Inventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgriInventoryResponse {
    private Long id;
//    private FarmProductsResponse farmProducts;
    private Double unitsAvailable;
    private Double unitsReserved;
    private Double unitsSold;
}
