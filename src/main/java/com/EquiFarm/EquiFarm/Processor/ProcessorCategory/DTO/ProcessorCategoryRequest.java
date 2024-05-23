package com.EquiFarm.EquiFarm.Processor.ProcessorCategory.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessorCategoryRequest {
    @NotNull(message = "Processor category is required.")
    private String processorCategory;
    private String description;
}
