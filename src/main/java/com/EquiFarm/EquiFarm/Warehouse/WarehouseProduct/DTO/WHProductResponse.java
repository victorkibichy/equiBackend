package com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.DTO;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.FarmProductsResponse;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.DTO.QualityCheckResponse;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.QualityCheck;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WHProductResponse {
//    private FarmProductsResponse farmProduct;
    private Long id;
    private String productLabel;
    private boolean qualityChecked;
    private Character available;
    private Character perishable;
    private Character verified;
    private Double lowStockLimit;
    private LocalDate expiryDate;
    private List<QualityCheckResponse> qualityChecks;
}
