package com.EquiFarm.EquiFarm.Processor.ProcessorCategory.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessorCategoryResponse {
    private Long id;
    private String processorCategory;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
