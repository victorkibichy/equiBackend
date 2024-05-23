package com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessorSubCategoryRequest {
    @NotNull(message = "Processor SubCategory is required.")
    private String processorSubCategory;
    private String description;
    private Long processorCategoryId;
}
