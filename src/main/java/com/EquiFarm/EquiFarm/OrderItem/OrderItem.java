package com.EquiFarm.EquiFarm.OrderItem;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsUnitsOfMeasurements;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Order_Item")
public class OrderItem extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmProduct_id")
    private FarmProducts farmProduct;

    @Column(name = "unit_price")
    @Builder.Default
    private Double unitPrice = 0.00;

    @Column(name = "quantity")
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "paid", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isPaid = false;

    @Column(name = "ordered", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isOrdered = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measurement", length = 26)
    private FarmProductsUnitsOfMeasurements unitOfMeasurements;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        updateUnitPrice();
    }

    public FarmProducts getFarmProduct() {
        return farmProduct;
    }

    public void setFarmProduct(FarmProducts farmProduct) {
        this.farmProduct = farmProduct;
        updateUnitPrice();
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitOfMeasurements(FarmProductsUnitsOfMeasurements unitsOfMeasurements) {
        this.unitOfMeasurements = unitsOfMeasurements;
    }

    public FarmProductsUnitsOfMeasurements getUnitsOfMeasurements() {
        return unitOfMeasurements;
    }

    private void updateUnitPrice() {
        if (quantity != null && farmProduct != null) {
            Double farmProductUnitPrice = farmProduct.getPricePerUnit();
            unitPrice = quantity * farmProductUnitPrice;
        } else {
            unitPrice = 0.00;
        }
    }

}
