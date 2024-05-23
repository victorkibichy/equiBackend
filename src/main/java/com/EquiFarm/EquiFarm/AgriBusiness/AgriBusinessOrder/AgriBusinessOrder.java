package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrderItem.AgriBusinessOrderItem;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "AgriBusiness Orders")
public class AgriBusinessOrder extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JoinTable(name = "agribusiness_order_agribusiness_order_items", joinColumns = @JoinColumn(name = "agribusiness_orderitem_id"))
    private List<AgriBusinessOrderItem> agriBusinessOrderItems;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 56)
    private AgriBusinessOrderStatus agriBusinessOrderStatus;

    @Column(name = "order_id", length = 20)
    private String agriBusinessOrderId;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "total_amount")
    @Builder.Default
    @Transient
    private Double totalAmount = 0.00;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ordered", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isOrdered = false;

    @Column(name = "paid", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isPaid = false;

    public   Double getTotalAmount() {
        totalAmount = 0.0; //initialize totalAmount variable to 0.0
        if (agriBusinessOrderItems != null && !agriBusinessOrderItems.isEmpty()) { //check if order items is not null and not empty
            for (AgriBusinessOrderItem agriBusinessOrderItem : agriBusinessOrderItems) { //iterate through each orderitem in the orderitem list
                totalAmount += agriBusinessOrderItem.getUnitPrice(); //add unit price of the orderitem to the totalAmount
            }
        }
        return totalAmount; //return the calculated total amount
    }
}



