package com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct;

import com.EquiFarm.EquiFarm.Driver.Driver;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.Warehouse.Warehouse;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.QualityCheck;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "warehouse_product")
public class WarehouseProduct extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false)
    private Long id;
    private String productLabel;
    private boolean available = false;
    private boolean perishable = false;
    private boolean qualityChecked = false;
    private boolean verified = false;
    private Double availableUnits;
    private Double lowStockLimit;
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private WHProductStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_product_id")
    private FarmProducts farmProduct;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "quality_check_id")
//    private QualityCheck qualityCheck;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "warehouse_product_checks", joinColumns = @JoinColumn(name = "quality_check_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "warehouse_product_id", referencedColumnName = "id"))
    private List<QualityCheck> qualityChecks;
}
