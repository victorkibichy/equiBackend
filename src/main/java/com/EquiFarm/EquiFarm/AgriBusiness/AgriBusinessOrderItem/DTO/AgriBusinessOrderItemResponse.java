package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrderItem.DTO;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.DTO.AgriBusinessProductResponse;
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
public class AgriBusinessOrderItemResponse {
    private Long id;
    private AgriBusinessProductResponse farmProduct;
    private Double unitPrice;
    private Integer quantity;
    private Boolean isPaid;
    private Boolean isOrdered;
    private com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductsUnitsOfMeasurements unitOfMeasurements;
    private UserResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
