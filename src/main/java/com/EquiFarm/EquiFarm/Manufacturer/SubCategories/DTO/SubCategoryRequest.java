package com.EquiFarm.EquiFarm.Manufacturer.SubCategories.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryRequest {
    @NotNull(message = "SubCategory is required.")
    private String subCategory;
    private String description;
    private Long categoryId;
}
