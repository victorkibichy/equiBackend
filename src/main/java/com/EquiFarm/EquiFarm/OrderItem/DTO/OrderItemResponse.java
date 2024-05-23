package com.EquiFarm.EquiFarm.OrderItem.DTO;

import java.time.LocalDateTime;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsUnitsOfMeasurements;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.FarmProductsResponse;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    private Long id;
    private FarmProductsResponse farmProduct;
    private Double unitPrice;
    private Integer quantity;
    private Boolean isPaid;
    private Boolean isOrdered;
    private FarmProductsUnitsOfMeasurements unitOfMeasurements;
    private UserResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
