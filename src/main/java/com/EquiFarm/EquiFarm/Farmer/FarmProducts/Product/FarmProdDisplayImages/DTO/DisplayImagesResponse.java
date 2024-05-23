package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DTO;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisplayImagesResponse {
    private Long id;
    private String displayImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
