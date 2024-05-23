package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessProductCategoryResponse {
    private Long id;
    private String productCategory;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
