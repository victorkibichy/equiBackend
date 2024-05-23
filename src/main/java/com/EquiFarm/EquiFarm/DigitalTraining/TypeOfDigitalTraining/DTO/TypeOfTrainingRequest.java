package com.EquiFarm.EquiFarm.DigitalTraining.TypeOfDigitalTraining.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeOfTrainingRequest {
    private String typeOfDigitalTraining;
    private String description;

}
