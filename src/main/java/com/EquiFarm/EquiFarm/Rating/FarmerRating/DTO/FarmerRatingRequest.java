package com.EquiFarm.EquiFarm.Rating.FarmerRating.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmerRatingRequest {
    private Long userId;
    private Long farmerId;
    private int stars;
    private String comment;
}






