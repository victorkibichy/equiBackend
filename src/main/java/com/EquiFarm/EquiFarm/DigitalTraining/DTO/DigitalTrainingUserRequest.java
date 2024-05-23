package com.EquiFarm.EquiFarm.DigitalTraining.DTO;

import com.EquiFarm.EquiFarm.utils.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DigitalTrainingUserRequest {
    private String trainingName;
    private LocalDateTime startDate;
    private LocalDateTime completionDate;
    private Boolean completed;
    private String trainingResources;
    private String description;
    private String videoUrl;
    private String imageUrl;
}
