package com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.DTO;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryResponse {
    private Long id;
    private String productCategory;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
