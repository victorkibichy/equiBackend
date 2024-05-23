package com.EquiFarm.EquiFarm.DeliveryAddress.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAddressRequest {
    private String addressName;
    private Double latitude;
    private Double longitude;
    private boolean isDefaultAddress;
    private String address;
    private Long userId;
}
