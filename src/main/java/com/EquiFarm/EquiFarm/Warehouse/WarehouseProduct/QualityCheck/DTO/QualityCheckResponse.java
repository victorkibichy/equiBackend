package com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.DTO;

import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.QualityCheckResult;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualityCheckResponse {
    private Long id;
    private String checkName;
    private String description;
    private QualityCheckResult qualityCheckResult;
    private String comments;
}
