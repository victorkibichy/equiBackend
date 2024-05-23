package com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.ProductCategory;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.WarehouseProduct;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quality_check")
public class QualityCheck extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false)
    private Long id;
    private String checkName;
    private String description;
    @Enumerated(EnumType.STRING)
    private QualityCheckResult qualityCheckResult;
    private String comments;
    private Long productCategoryId;

    @ManyToMany(mappedBy = "qualityChecks", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<WarehouseProduct> warehouseProducts;
}