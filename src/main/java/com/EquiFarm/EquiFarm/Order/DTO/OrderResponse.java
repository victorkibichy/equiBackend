package com.EquiFarm.EquiFarm.Order.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import com.EquiFarm.EquiFarm.Order.OrderStatus;
import com.EquiFarm.EquiFarm.OrderItem.DTO.OrderItemResponse;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderId;
    private OrderStatus orderStatus;
    private Double totalAmount;
    private Boolean isPaid;
    private List<OrderItemResponse> orderItems;
    private UserResponse user;
    private LocalDateTime orderDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
