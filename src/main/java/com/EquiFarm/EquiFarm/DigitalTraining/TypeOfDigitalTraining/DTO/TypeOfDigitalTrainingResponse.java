package com.EquiFarm.EquiFarm.DigitalTraining.TypeOfDigitalTraining.DTO;

import com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices.DTO.TypeOfServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeOfDigitalTrainingResponse {
    private Long id;
    private String typeOfDigitalTraining;
    private String description;

}
