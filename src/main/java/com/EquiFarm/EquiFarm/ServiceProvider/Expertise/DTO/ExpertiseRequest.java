package com.EquiFarm.EquiFarm.ServiceProvider.Expertise.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpertiseRequest {
    private String expertiseName;
    private String description;
}
