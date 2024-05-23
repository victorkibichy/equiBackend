package com.EquiFarm.EquiFarm.TempTransactions;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponse {
    @JsonRawValue
    private String message;
    private String status;
    private String transDate;
    private String transId;

}

