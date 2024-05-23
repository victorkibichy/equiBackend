package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessWishList.DTO;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.DTO.AgriBusinessProductResponse;
import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessWishListResponse {
    private Long id;
    private List<AgriBusinessProductResponse> agriBusinessProduct;
    private UserResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
