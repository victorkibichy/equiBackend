package com.EquiFarm.EquiFarm.Transactions.PartTrans;

import java.time.LocalDateTime;
import java.util.List;

import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWallet;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.Transactions.TransactionStatus;
import com.EquiFarm.EquiFarm.Wallet.Currency;
import com.EquiFarm.EquiFarm.Wallet.Wallet;

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
import jakarta.persistence.Table;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PartTrans")
public class PartTrans extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "debit_wallet_id")
    private Wallet debitWallet;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "credit_wallet_id")
    private Wallet creditWallet;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "escrowWallet_id")
    private EscrowWallet escrowWallet;

    @Column(name = "transaction_amount")
    private double transactionAmount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    @Builder.Default
    private Currency currency = Currency.KES;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "transaction_code")
    private String transactionCode;

    @Column(name = "transaction_date", updatable = false)
    private LocalDateTime transactionDate;

    @Column(name = "wallet_balance")
    private Double walletBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PartTransTypes partTranType;
}
