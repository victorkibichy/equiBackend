package com.EquiFarm.EquiFarm.Rating.AgriBusinessRating.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessRatingRequest {
    private Long userId;
    private Long agriBusinessId;
    private int stars;
    private String comment;
}
