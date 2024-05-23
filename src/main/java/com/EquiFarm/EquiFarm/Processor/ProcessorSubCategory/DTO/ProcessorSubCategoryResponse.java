package com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessorSubCategoryResponse {
    private Long id;
    private String processorSubCategory;
    private String description;
    private Long processorCategoryId;
//    private ProcessorCategoryResponse processorCategoryResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
