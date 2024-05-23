package com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory;

import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Product_Category")
public class ProductCategory extends TrackingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "product_category", nullable = false, length = 120)
    private String productCategory;

    @Column(nullable = true, length = 250)
    private String description;
}
