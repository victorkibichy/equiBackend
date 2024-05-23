package com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsUnitsOfMeasurements;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.ProductCategory;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;



@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Types_of_Products")
public class TypesOfProducts extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "type_of_product", nullable = false, length = 120)
    private String typeOfProduct;

    @Column(nullable = true, length = 250)
    private String description;

    @Column(nullable = false, length = 255)
    private FarmProductsUnitsOfMeasurements units;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "productcategory_id")
    private ProductCategory category;
}
