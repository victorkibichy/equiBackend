package com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts.DTO;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsUnitsOfMeasurements;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeOfProductRequest {
    private String typeOfProduct;
    private String description;
    private FarmProductsUnitsOfMeasurements units;
    private Long categoryId;
}
