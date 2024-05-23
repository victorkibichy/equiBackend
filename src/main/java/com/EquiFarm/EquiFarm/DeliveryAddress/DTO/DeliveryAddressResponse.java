package com.EquiFarm.EquiFarm.DeliveryAddress.DTO;

import java.time.LocalDateTime;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAddressResponse {
    private Long id;
    private String addressName;
    private Double latitude;
    private Double longitude;
    private boolean isDefaultAddress;
    private String address;
    private UserResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
