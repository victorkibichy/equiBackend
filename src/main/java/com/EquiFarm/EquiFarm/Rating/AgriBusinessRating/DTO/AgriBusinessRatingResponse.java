package com.EquiFarm.EquiFarm.Rating.AgriBusinessRating.DTO;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessRatingResponse {
    private Long id;
    private UserResponse user;
    private Long userId;
    private int stars;
    private String comment;

}
