package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrderItem;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProduct;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductsUnitsOfMeasurements;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.user.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "AgriBusiness_Order_Item")
public class AgriBusinessOrderItem extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agriBusiness_Product_id")
    private AgriBusinessProduct agriBusinessProduct;

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
    private AgriBusinessProductsUnitsOfMeasurements unitOfMeasurements;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        updateUnitPrice();
    }

    public AgriBusinessProduct getAgriBusinessProduct() {
        return agriBusinessProduct;
    }

    public void setAgriBusinessProduct(AgriBusinessProduct agriBusinessProduct) {
        this.agriBusinessProduct = agriBusinessProduct;
        updateUnitPrice();
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitOfMeasurements(com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductsUnitsOfMeasurements unitsOfMeasurements) {
        this.unitOfMeasurements = unitsOfMeasurements;
    }

    public com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductsUnitsOfMeasurements getUnitsOfMeasurements() {
        return unitOfMeasurements;
    }

    private void updateUnitPrice() {
        if (quantity != null && agriBusinessProduct != null) {
            Double agriBusinessProductUnitPrice = agriBusinessProduct.getPricePerUnit();
            unitPrice = quantity * agriBusinessProductUnitPrice;
        } else {
            unitPrice = 0.00;
        }
    }
}
