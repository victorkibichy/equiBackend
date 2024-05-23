package com.EquiFarm.EquiFarm.Rating.AgriBusinessProductRating.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessProductRatingRequest {
    private Long userId;
    private Long agriBusinessProductId;
    private int stars;
    private String comment;







}
