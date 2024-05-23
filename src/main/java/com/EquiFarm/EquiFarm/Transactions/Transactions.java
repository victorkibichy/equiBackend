package com.EquiFarm.EquiFarm.Transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.EquiFarm.EquiFarm.Checkout.Checkout;
import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWallet;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.PartTrans;
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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Transactions")
public class Transactions extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;
    @Column(name = "transaction_code")
    private String transactionCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private TransactionType transactionType;

    @Column(name = "transaction_date", updatable = false)
    private LocalDateTime transactionDate;

    @Column(name = "transaction_amount")
    private Double transactionAmount;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "trans_sender_wallet_id")
    private Wallet senderWallet;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "trans_receiver_wallet_id")
    private List<Wallet> receiverWallet;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "trans_escrow_wallet_id")
    private EscrowWallet escrowWallet;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "trans_checkout_id")
    private Checkout checkout;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    @Builder.Default
    private Currency currency = Currency.KES;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;
    // --> Mpesa service
    private String merchantRequestId;
    private String checkoutRequestID;
    private String responseCode;

    private String responseDescription;
    private String customerMessage;
    private String mpesaReceiptNumber;

    private String paymentTransactionDate;
    private String conversationID;
    private String originatorConversationID;
    // fin service
    @Column(name = "request_Id")
    private String requestId;
    @Column(name = "trans_Id")
    private String transId;
    @Column(name = "account_number")
    private String accountNumber;
    // part transactions
    @OneToMany(targetEntity = PartTrans.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_id", referencedColumnName = "id")
    private List<PartTrans> partTrans;
}
