package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.TypeOfAgriBusinessProduct.DTO;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory.DTO.AgriBusinessProductCategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeOfAgriBusinessProductResponse {
    private Long id;
    private String typeOfAgriBusinessProduct;
    private String description;
    private com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductsUnitsOfMeasurements units;
    private AgriBusinessProductCategoryResponse category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
