package com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryRequest {
    private String productCategory;
    private String description;
}
