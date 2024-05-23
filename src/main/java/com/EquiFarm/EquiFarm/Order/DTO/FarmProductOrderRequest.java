package com.EquiFarm.EquiFarm.Order.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmProductOrderRequest {
    private Long farmProductId;
    private Integer quantity;
}
