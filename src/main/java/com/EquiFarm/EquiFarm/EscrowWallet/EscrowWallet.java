package com.EquiFarm.EquiFarm.EscrowWallet;

import java.time.LocalDateTime;

import com.EquiFarm.EquiFarm.Checkout.Checkout;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.Wallet.Currency;
import com.EquiFarm.EquiFarm.Wallet.Wallet;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Escrow_Wallet")
public class EscrowWallet extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "wallet_number", length = 20, nullable = false, unique = true, updatable = false)
    private String walletNumber;

    @Column(name = "balance")
    @Builder.Default
    private Double balance = 0.0;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "checkout_id")
    private Checkout checkout;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    @Builder.Default
    private Currency currency = Currency.KES;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    @Builder.Default
    private EscrowStatus status = EscrowStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private TypeOfTrade typeOfTrade;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sender_wallet_id")
    private Wallet senderWallet;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "receiver_wallet_id")
    private Wallet receiverWallet;

    @Column(name = "is_opened", columnDefinition = "boolean default true")
    @Builder.Default
    private boolean isOpened = true;
    
    @Column(name = "released_at", updatable = false)
    private LocalDateTime releasedAt;
}