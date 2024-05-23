package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessChekout;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder.AgriBusinessOrder;
import com.EquiFarm.EquiFarm.DeliveryAddress.DeliveryAddress;
import com.EquiFarm.EquiFarm.EscrowWallet.TypeOfTrade;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "AgriBusiness_Checkout")
public class AgriBusinessCheckout extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agribusiness_order_id", unique = true, nullable = false)
    private AgriBusinessOrder agriBusinessOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;

    @Column(name = "order_note")
    private String orderNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 56)
    private AgriBusinessCheckoutStatus agriBusinessCheckoutStatus;

    @Column(name = "agribusiness_order_amount")
    @Builder.Default
    @Transient
    private Double agriBusinessOrderAmount = 0.00;

//    @Column(name = "delivery_fee")
//    @Builder.Default
//    @Transient
//    private Double deliveryFee = 0.00;

    @Column(name = "total_amount")
    @Builder.Default
    @Transient
    private Double totalAmount = 0.00;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private TypeOfTrade typeOfTrade;

//    @ManyToMany
//    @JoinTable(name = "checkout_driver", joinColumns = @JoinColumn(name = "agribusiness_checkout_id"), inverseJoinColumns = @JoinColumn(name = "driver_id"))
//    private List<Driver> drivers;
//
//    @Column(name = "expected_arrival_date", updatable = false)
//    private LocalDateTime expectedArrivalDate;

    public Double getAgriBusinessOrderAmount() {
        agriBusinessOrderAmount = 0.0;
        if (agriBusinessOrder != null) {
            agriBusinessOrderAmount = agriBusinessOrder.getTotalAmount();
            System.out.println("AgriBusiness order amount: " + agriBusinessOrderAmount);

        }
        return agriBusinessOrderAmount;
    }

//    public Double getDeliveryFee() {
//        if (deliveryFee == null) {
//            deliveryFee = 0.0;
//        }
//        return deliveryFee;
//    }

//    public Double getTotalAmount() {
//        if (totalAmount == null || totalAmount.equals(0.0)) {
//            if (agriBusinessOrder != null) {
//                totalAmount = agriBusinessOrder.getTotalAmount();
//            } else {
//                totalAmount = 0.0;
//            }
//        }
//        return totalAmount;
//    }
}