package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemoveAgriBusinessOrderItemRequest {
    private List<Long> agriBusinessOrderItemsIds;
}
