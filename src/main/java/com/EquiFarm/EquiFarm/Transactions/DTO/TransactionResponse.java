package com.EquiFarm.EquiFarm.Transactions.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.EquiFarm.EquiFarm.Checkout.DTO.CheckoutResponse;
import com.EquiFarm.EquiFarm.EscrowWallet.DTO.EscrowWalletResponse;
import com.EquiFarm.EquiFarm.Transactions.TransactionStatus;
import com.EquiFarm.EquiFarm.Transactions.TransactionType;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.DTO.PartTransResponse;
import com.EquiFarm.EquiFarm.Wallet.Currency;
import com.EquiFarm.EquiFarm.Wallet.DTO.WalletResponse;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Long id;
    private String transactionCode;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;
    private Double transactionAmount;
    private WalletResponse senderWallet;
    private List<WalletResponse> receiverWallet;
    private EscrowWalletResponse escrowWallet;
    private CheckoutResponse checkout;
    private Currency currency;
    private TransactionStatus status;
    private String merchantRequestId;
    private String checkoutRequestID;
    private String responseCode;
    private String responseDescription;
    private String customerMessage;
    private String mpesaReceiptNumber;
    private String paymentTransactionDate;
    private String conversationID;
    private String originatorConversationID;
    private String requestId;
    private String transId;
    private String accountNumber;
    private List<PartTransResponse> partTrans;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
