package com.EquiFarm.EquiFarm.Insurance.TypeOfInsurance.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeOfInsuranceResponse {
    private Long id;
    private String typeOfInsurance;
    private String description;
}