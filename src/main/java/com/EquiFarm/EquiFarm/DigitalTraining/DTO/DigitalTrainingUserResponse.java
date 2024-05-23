package com.EquiFarm.EquiFarm.DigitalTraining.DTO;

import com.EquiFarm.EquiFarm.DigitalTraining.TypeOfDigitalTraining.DTO.TypeOfDigitalTrainingResponse;
import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import com.EquiFarm.EquiFarm.utils.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DigitalTrainingUserResponse {

    private String trainingName;
    private LocalDateTime startDate;
    private LocalDateTime completionDate;
    private Boolean completed;
    private String trainingResources;
    private String description;
    private String videoUrl;
    private String imageUrl;
//    private TypeOfDigitalTrainingResponse typeOfDigitalTraining;

}
