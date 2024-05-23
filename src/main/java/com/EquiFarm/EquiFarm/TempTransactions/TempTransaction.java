package com.EquiFarm.EquiFarm.TempTransactions;

import com.EquiFarm.EquiFarm.TempTransactions.TempPatrans.TempPartTrans;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempTransaction extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;
    private Long orderId;
    private Long productId;
    private String orderItemName;
    private Long buyerUserId;
    private Long sellerUserId;
    private User buyer;
    private User seller;
    private String buyerName;
    private String buyerRole;

    private String sellerName;
    private String sellerRole;

    private Double totalAmount;
    private String transactionId;
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private TransStatus transStatus;

    @OneToMany(targetEntity = TempPartTrans.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "temp_transaction_id", referencedColumnName = "id")
    private List<TempPartTrans> tempPartTrans;
}
