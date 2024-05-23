package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product;


import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DisplayImages;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.ProductCategory;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts.TypesOfProducts;
import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Farm_Products")
public class FarmProducts extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "typeOfProduct_id")
    private TypesOfProducts typeOfProduct;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "productCategory_id")
    private ProductCategory category;

    @Column(nullable = true, length = 250, name = "product_description")
    private String product_Description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "farm_product_display_images", joinColumns = @JoinColumn(name = "farm_product_id"), inverseJoinColumns = @JoinColumn(name = "display_image_id"))
    @Builder.Default
    private List<DisplayImages> displayImages = new ArrayList<>();

    @Lob
    @Column(name = "product_image", nullable = true, length = 16777215)
    private String productImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    @Column(name = "latitude", nullable = true)
    @Builder.Default
    private Double latitude = -0.0236;
    @Column(name = "longitude", nullable = true)
    @Builder.Default
    private Double longitude = 37.9062;
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measurement", nullable = false, length = 26)
    private FarmProductsUnitsOfMeasurements unitOfMeasurements;
    @Column(name = "units_available", nullable = false)
    private Double unitsAvailable;
    @Column(name = "price_per_unit", nullable = false)
    @Builder.Default
    private Double pricePerUnit = 0.00;

    @Column(name = "verified", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "on_stock", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean onStock = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "selling_point")
    private SellingPoint sellingPoint;


    @Column(name = "availability_date")
    @JsonIgnore
    private LocalDate availabilityDate;

    @Column(name = "pre_listed", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isPreListed = false;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "farm_product_chains",
            joinColumns = @JoinColumn(name = "farm_product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "value_chain_id", referencedColumnName = "id"))
    private List<ValueChain> valueChains;


//    @Column(name = "amount")
//    private Double amount;

    @Column(name = "expiration_date")
    @JsonIgnore
    private LocalDate expirationDate;

    @Column(name= "discount")
    private Double discount;



    // @Column(name = "product_name", length = 256, nullable = false)
    // private String productName;
}
