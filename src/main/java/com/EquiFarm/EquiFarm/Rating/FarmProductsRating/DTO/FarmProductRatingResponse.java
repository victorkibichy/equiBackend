package com.EquiFarm.EquiFarm.Rating.FarmProductsRating.DTO;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmProductRatingResponse {
    private  Long id;
    private Long userId;
    private UserResponse user;
    private Long farmProductId;
    private int stars;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
