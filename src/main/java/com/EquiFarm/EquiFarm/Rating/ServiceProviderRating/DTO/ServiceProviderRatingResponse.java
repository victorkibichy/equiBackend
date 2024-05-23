package com.EquiFarm.EquiFarm.Rating.ServiceProviderRating.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProviderRatingResponse {
    private  Long id;
    private Long userId;
    private Long serviceProviderId;
    private int stars;
    private String comment;
}
