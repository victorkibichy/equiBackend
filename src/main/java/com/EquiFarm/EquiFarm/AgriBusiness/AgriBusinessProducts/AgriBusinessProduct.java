package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusiness;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProdDisplayImage.AgriBusinessDisplayImages;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory.AgriBusinessProductCategory;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.TypeOfAgriBusinessProduct.TypeOfAgriBusinessProduct;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.SellingPoint;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "AgriBusiness_Products")
public class AgriBusinessProduct extends TrackingEntity {
    @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "id", updatable = false)
        private Long id;

        @ManyToOne(optional = true, fetch = FetchType.LAZY)
        @JoinColumn(name = "typeOfAgriBuisnessProduct_id")
        private TypeOfAgriBusinessProduct typeOfAgriBuisnessProduct;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
        @JoinColumn(name = "agriBusinessProductCategory_id")
        private AgriBusinessProductCategory agriBusinessProductCategory;

        @Column(nullable = true, length = 250, name = "agriBusiness_product_description")
        private String agriBusiness_Product_Description;

        @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinTable(name = "agribusiness_product_display_images",
                joinColumns = @JoinColumn(name = "agribusiness_product_id"),
                inverseJoinColumns = @JoinColumn(name = "agribusiness_display_image_id"))
        @Builder.Default
        private List<AgriBusinessDisplayImages> agriBusinessDisplayImages = new ArrayList<>();

        @Lob
        @Column(name = "agribusiness_product_image", nullable = true, length = 16777215)
        private String agriBusinessProductImage;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "agribusiness_id")
        private AgriBusiness agriBusiness;

        @Column(name = "latitude", nullable = true)
        @Builder.Default
        private Double latitude = -0.0236;
        @Column(name = "longitude", nullable = true)
        @Builder.Default
        private Double longitude = 37.9062;
        @Enumerated(EnumType.STRING)
        @Column(name = "unit_of_measurement", nullable = false, length = 26)
        private AgriBusinessProductsUnitsOfMeasurements unitOfMeasurements;
        @Column(name = "units_available", nullable = false)
        private Double unitsAvailable;
        @Column(name = "price_per_unit", nullable = false)
        @Builder.Default
        private Double pricePerUnit = 0.00;

        @Column(name = "verified", columnDefinition = "boolean default false")
        @Builder.Default
        private Boolean isVerified = false;

        @Column(name = "on_stock", columnDefinition = "boolean default false")
        @Builder.Default
        private Boolean onStock = false;

        @Enumerated(EnumType.STRING)
        @Column(name = "selling_point")
        private SellingPoint sellingPoint;

        @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        @JoinTable(name = "agribusiness_product_chains",
                joinColumns = @JoinColumn(name = "agribusiness_product_id", referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(name = "value_chain_id", referencedColumnName = "id"))
        private List<ValueChain> valueChains;
}
