package com.EquiFarm.EquiFarm.Transactions.PartTrans.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.EquiFarm.EquiFarm.EscrowWallet.DTO.EscrowWalletResponse;
import com.EquiFarm.EquiFarm.Transactions.TransactionStatus;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.PartTransTypes;
import com.EquiFarm.EquiFarm.Wallet.Currency;
import com.EquiFarm.EquiFarm.Wallet.DTO.WalletResponse;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartTransResponse {
    private Long id;
    private String transactionCode;
    private double transactionAmount;
    private Currency currency;
    private WalletResponse debitWallet;
    private WalletResponse creditWallet;
    private EscrowWalletResponse escrowWallet;
    private TransactionStatus status;
    private LocalDateTime transactionDate;
    private Double walletBalance;
    private PartTransTypes partTranType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
