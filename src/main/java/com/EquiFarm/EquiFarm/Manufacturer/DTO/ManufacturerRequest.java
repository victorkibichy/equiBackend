package com.EquiFarm.EquiFarm.Manufacturer.DTO;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.CordinatesDTO;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.WorkingHoursDTO;
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
public class ManufacturerRequest {
    private Long id;
    private UserResponse user;
    private String idNumber;
    private String bio;
    private Gender gender;
    private String licenceNumber;
    private String manufacturerName;
    private String manufacturerEmail;
    private String manufacturerPhoneNumber;
    private String manufacturerLogo;
    private String manufacturerDescription;
    private String profilePicture;
    private Boolean manufacturerVerified;
    private Long categoryId;
    private Long subCategoryId;
    private List<Long> valueChainIds;
    @Embedded
    private WorkingHoursDTO workingHours;
    private Set<DayOfWeek> workingDays;
    @Embedded
    private CordinatesDTO businessLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
