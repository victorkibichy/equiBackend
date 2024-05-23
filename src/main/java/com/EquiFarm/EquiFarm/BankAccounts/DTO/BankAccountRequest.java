package com.EquiFarm.EquiFarm.BankAccounts.DTO;

import com.EquiFarm.EquiFarm.BankAccounts.Banks;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountRequest {
    private Banks bank;
    private String accountNumber;
    private String accountName;
    private Long userId;
}
