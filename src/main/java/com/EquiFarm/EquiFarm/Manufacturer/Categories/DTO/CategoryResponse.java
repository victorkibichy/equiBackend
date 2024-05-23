package com.EquiFarm.EquiFarm.Manufacturer.Categories.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String category;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
