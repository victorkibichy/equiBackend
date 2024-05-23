package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.Inventory;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProduct;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory")
public class AgriInventory extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "agriProduct_id")
    private AgriBusinessProduct agriBusinessProduct;

    private Double availableUnits;

    private Double reservedUnits;

    private Double soldUnits;
}
