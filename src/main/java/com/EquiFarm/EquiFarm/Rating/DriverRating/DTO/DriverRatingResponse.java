package com.EquiFarm.EquiFarm.Rating.DriverRating.DTO;

import com.EquiFarm.EquiFarm.Driver.DTO.DriverResponse;
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
public class DriverRatingResponse {
    private  Long id;
    private UserResponse user;
    private Long userId;
//    private Long driverId;
//    private UserResponse user;
//    private DriverResponse driver;
    private int stars;
    private String comment;

//    private LocalDateTime createdAt;
//    private LocalDateTime ratingDateTime;
}
