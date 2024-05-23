package com.EquiFarm.EquiFarm.Farmer.Farm.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.EquiFarm.EquiFarm.Farmer.DTO.FarmersResponse;
import com.EquiFarm.EquiFarm.Farmer.Farm.FarmOwnership;
import com.EquiFarm.EquiFarm.Farmer.Farm.SoilType;
import com.EquiFarm.EquiFarm.Farmer.Farm.SourceOfWater;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmResponse {
    private Long id;
    private FarmOwnership typeOfOwnership;
    private String titleNumber;
    private String titleDeed;
    private String farmSize;
    private Double latitude;
    private Double longitude;
    private Double farmIncome;
    private SoilType soilType;
    private SourceOfWater sourceOfWater;
    private String farmHistory;
    private List<FarmersResponse> farmers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
