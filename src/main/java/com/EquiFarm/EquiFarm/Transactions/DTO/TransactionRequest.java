package com.EquiFarm.EquiFarm.Transactions.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.EquiFarm.EquiFarm.Transactions.TransactionType;
import com.EquiFarm.EquiFarm.Transactions.PartTrans.DTO.PartTransRequest;
import com.EquiFarm.EquiFarm.Wallet.Currency;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private TransactionType transactionType;
    private Double transactionAmount;
    private Long debitWalletId;
    private List<Long> creditWalletId;
    private Long escrowWalletId;
    private Long checkoutId;
    private Currency currency;
    private List<PartTransRequest> partrans;
}
