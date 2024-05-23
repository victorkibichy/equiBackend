package com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts.DTO;

import java.time.LocalDateTime;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsUnitsOfMeasurements;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.DTO.ProductCategoryResponse;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeOfProductResponse {
    private Long id;
    private String typeOfProduct;
    private String description;
    private FarmProductsUnitsOfMeasurements units;
    private ProductCategoryResponse category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
