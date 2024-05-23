package com.EquiFarm.EquiFarm.Transactions.PartTrans.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.EquiFarm.EquiFarm.Transactions.PartTrans.PartTransTypes;
import com.EquiFarm.EquiFarm.Wallet.Currency;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartTransRequest {
    private Long debitWalletId;
    private Long creditWalletId;
    private Long escrowWalletId;
    private Currency currency;
    private LocalDateTime transactionDate;
    private double transactionAmount;
    private PartTransTypes partTranType;
}
