package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Inventory;

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
public class Inventory extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "farmProduct_id")
    private FarmProducts farmProducts;

    private Double availableUnits;

    private Double reservedUnits;

    private Double soldUnits;
}
