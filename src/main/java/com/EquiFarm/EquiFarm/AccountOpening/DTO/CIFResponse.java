package com.EquiFarm.EquiFarm.AccountOpening.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CIFResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String nationalId;
    private String Cifld;
    private String AcctNum;
    private String LienAmt;
    private String acid;
    private String crncyCode;
    private String acctName;
    private String address1;
    private String address2;
    private String phoneNumber2;
    private String code;
    private String refCode;
    private String schemeType;
    private String cntryCode;
    private String ClrBalamt;
    private String solld;
}
