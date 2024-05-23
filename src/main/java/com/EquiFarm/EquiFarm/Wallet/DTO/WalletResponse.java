package com.EquiFarm.EquiFarm.Wallet.DTO;

import java.time.LocalDateTime;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import com.EquiFarm.EquiFarm.Wallet.Currency;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse {
    private Long id;
    private Long walletNumber;
    private Double balance;
    private Currency currency;
    private UserResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
