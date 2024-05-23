package com.EquiFarm.EquiFarm.Checkout;

import java.time.LocalDateTime;
import java.util.List;

import com.EquiFarm.EquiFarm.DeliveryAddress.DeliveryAddress;
import com.EquiFarm.EquiFarm.Driver.Driver;
import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWallet;
import com.EquiFarm.EquiFarm.EscrowWallet.TypeOfTrade;
import com.EquiFarm.EquiFarm.Order.Order;
import com.EquiFarm.EquiFarm.Order.OrderStatus;
import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Checkout")
public class Checkout extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    private Order order;

    // @Column(name = "delivery_address")
    // @Embedded
    // private Cordinates deliveryAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;

    @Column(name = "order_note")
    private String orderNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 56)
    private CheckoutStatus status;

    @Column(name = "order_amount")
    @Builder.Default
    @Transient
    private Double orderAmount = 0.00;

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
//    @JoinTable(name = "checkout_driver", joinColumns = @JoinColumn(name = "checkout_id"), inverseJoinColumns = @JoinColumn(name = "driver_id"))
//    private List<Driver> drivers;

//    @Column(name = "expected_arrival_date", updatable = false)
//    private LocalDateTime expectedArrivalDate;

    public Double getOrderAmount() {
        orderAmount = 0.0;
        if (order != null) {
            orderAmount = order.getTotalAmount();
            System.out.println("Order amount: " + orderAmount);

        }
        return orderAmount;
    }

//    public Double getDeliveryFee() {
//        if (deliveryFee == null) {
//            deliveryFee = 0.0;
//        }
//        return deliveryFee;
//    }

//    public Double getTotalAmount() {
//        if (totalAmount == null || totalAmount.equals(0.0)) {
//            if (order != null) {
//                totalAmount = order.getTotalAmount() + getDeliveryFee();
//            } else {
//                totalAmount = 0.0;
//            }
//        }
//        return totalAmount;
//    }

}
