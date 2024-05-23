package com.EquiFarm.EquiFarm.Rating.DriverRating.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverRatingRequest {
    private Long userId;
    private Long driverId;
    private int stars;
    private String comment;
}
