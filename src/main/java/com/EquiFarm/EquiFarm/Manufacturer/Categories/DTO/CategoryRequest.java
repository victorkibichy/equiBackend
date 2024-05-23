package com.EquiFarm.EquiFarm.Manufacturer.Categories.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    @NotNull(message = "Category is required.")
    private String category;
    private String description;
}
