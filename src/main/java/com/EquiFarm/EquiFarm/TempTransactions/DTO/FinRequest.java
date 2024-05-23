package com.EquiFarm.EquiFarm.TempTransactions.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinRequest {
    private String drAcc;
    private String crAcc;
    private String amount;
}
