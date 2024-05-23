package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.TypeOfAgriBusinessProduct;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory.AgriBusinessProductCategory;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductsUnitsOfMeasurements;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Types_of_Agribusiness_Products")
public class TypeOfAgriBusinessProduct extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "type_of_agribusiness_product", nullable = false, length = 120)
    private String typeOfAgriBusinessProduct;

    @Column(nullable = true, length = 250)
    private String description;

    @Column(nullable = false, length = 255)
    private AgriBusinessProductsUnitsOfMeasurements units;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "agribusiness_product_category_id")
    private AgriBusinessProductCategory category;
}
