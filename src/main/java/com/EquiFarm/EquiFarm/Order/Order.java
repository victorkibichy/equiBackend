package com.EquiFarm.EquiFarm.Order;

import java.time.LocalDateTime;
import java.util.List;

import com.EquiFarm.EquiFarm.OrderItem.OrderItem;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import jakarta.persistence.FetchType;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Orders")
public class Order extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JoinTable(name = "order_order_items", joinColumns = @JoinColumn(name = "orderitem_id"))
    private List<OrderItem> orderItems;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 56)
    private OrderStatus orderStatus;

    @Column(name = "order_id", length = 20)
    private String orderId;

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
        totalAmount = 0.0;
        if (orderItems != null && !orderItems.isEmpty()) {
            for (OrderItem orderItem : orderItems) {
                totalAmount += orderItem.getUnitPrice();
            }
        }
        return totalAmount;
    }
}
