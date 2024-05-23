package com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.DTO;

import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.QualityCheckResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualityCheckRequest {
    @NotNull(message = "checkName is required.")
    private String checkName;
    private String description;
    private QualityCheckResult qualityCheckResult;
    private String comments;
}
