package com.EquiFarm.EquiFarm.EscrowWallet.DTO;

import java.time.LocalDateTime;

import com.EquiFarm.EquiFarm.Checkout.DTO.CheckoutResponse;
import com.EquiFarm.EquiFarm.EscrowWallet.EscrowStatus;
import com.EquiFarm.EquiFarm.EscrowWallet.TypeOfTrade;
import com.EquiFarm.EquiFarm.Wallet.Currency;
import com.EquiFarm.EquiFarm.Wallet.DTO.WalletResponse;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EscrowWalletResponse {
    private Long id;
    private String walletNumber;
    private Double balance;
    private CheckoutResponse checkout;
    private Currency currency;
    private EscrowStatus status;
    private TypeOfTrade typeOfTrade;
    private WalletResponse senderWallet;
    private WalletResponse receiverWallet;
    private boolean isOpened;
    private LocalDateTime releasedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
