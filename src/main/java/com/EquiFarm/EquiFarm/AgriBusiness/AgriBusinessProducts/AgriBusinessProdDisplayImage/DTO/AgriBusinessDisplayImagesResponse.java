package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProdDisplayImage.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessDisplayImagesResponse {
    private Long id;
    private String agriBusinessDisplayImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

