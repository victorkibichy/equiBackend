package com.EquiFarm.EquiFarm.FarmTech.DTO;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.CordinatesDTO;
import com.EquiFarm.EquiFarm.ServiceProvider.WorkingHours;
import com.EquiFarm.EquiFarm.ValueChain.DTO.ValueChainResponse;
import com.EquiFarm.EquiFarm.utils.Gender;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmTechResponse {
    private Long id;
    private UserResponse user;
    private String idNumber;
    private String bio;
    private Gender gender;
    private String farmTechName;
    private String farmTechEmail;
    private String farmTechDescription;
    private String farmTechPhone;
    private String licenseNumber;
    private String farmTechLogo;
    private String farmTechCode;

    @Embedded
    private WorkingHours workingHours;

    private Set<DayOfWeek> workingDays;

    @Embedded
    private CordinatesDTO farmTechLocation;

    private Boolean farmTechVerified;
    private List<ValueChainResponse> valueChains;
    private String farmTechProfilePicture;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
