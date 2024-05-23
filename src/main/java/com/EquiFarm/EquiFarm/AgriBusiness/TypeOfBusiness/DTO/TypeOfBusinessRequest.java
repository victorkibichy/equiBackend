package com.EquiFarm.EquiFarm.AgriBusiness.TypeOfBusiness.DTO;

import javax.validation.constraints.NotNull;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeOfBusinessRequest {
    @NotNull(message = "Type of Business is required.")
    private String typeOfBusiness;
    private String description;
}
