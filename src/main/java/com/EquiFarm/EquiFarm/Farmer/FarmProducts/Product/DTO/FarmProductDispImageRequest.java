package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO;

import java.util.List;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmProductDispImageRequest {
    private List<Long> displayImagesIds;
}
