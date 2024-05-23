package com.EquiFarm.EquiFarm.Manufacturer.SubCategories.DTO;

import com.EquiFarm.EquiFarm.Manufacturer.Categories.DTO.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryResponse {
    private Long id;
    private String subCategory;
    private String description;
    private CategoryResponse category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
