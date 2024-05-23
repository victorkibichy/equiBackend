package com.EquiFarm.EquiFarm.AgriBusiness.TypeOfBusiness.DTO;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeOfBusinessResponse {
    private Long id;
    private String typeOfBusiness;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
