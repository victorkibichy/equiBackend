package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitsRequest {
    private Double units;
    private Long productId;
}
