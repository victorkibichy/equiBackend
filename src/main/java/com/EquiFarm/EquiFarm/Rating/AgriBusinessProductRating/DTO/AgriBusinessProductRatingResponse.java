package com.EquiFarm.EquiFarm.Rating.AgriBusinessProductRating.DTO;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessProductRatingResponse {
    private  Long id;
    private UserResponse user;
    private Long userId;
    private int stars;
    private String comment;

}
