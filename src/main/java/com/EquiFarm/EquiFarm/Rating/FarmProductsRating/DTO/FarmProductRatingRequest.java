package com.EquiFarm.EquiFarm.Rating.FarmProductsRating.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmProductRatingRequest {
    private Long userId;
    private Long farmProductId;
    private int stars;
    private String comment;
}
