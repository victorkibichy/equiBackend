package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessOrderRequest {
    private Long agriBusinessProductId;
    private Integer quantity;
}
