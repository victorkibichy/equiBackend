package com.EquiFarm.EquiFarm.BankAccounts.DTO;

import java.time.LocalDateTime;

import com.EquiFarm.EquiFarm.BankAccounts.Banks;
import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountResponse {
    private Long id;
    private Banks bank;
    private String accountNumber;
    private String accountName;
    private String accBal;
    private UserResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
