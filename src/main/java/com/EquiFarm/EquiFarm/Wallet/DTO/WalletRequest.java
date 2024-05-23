package com.EquiFarm.EquiFarm.Wallet.DTO;

import java.util.Currency;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletRequest {
    private Long walletNumber;
    private Double balance;
    private Currency currency;
}
