package com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WHProductRequest {
    private LocalDate expiryDate;
    private boolean perishable;
    private Double lowStockLimit;
}
