package com.EquiFarm.EquiFarm.ServiceProvider.DTO;

import com.EquiFarm.EquiFarm.Branch.DTO.BranchResponse;
import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
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
public class ServiceProviderUserResponse {
    private Long id;
    private UserResponse user;
    private String idNumber;
    private String bio;
    private Gender gender;
    private String businessName;
    private String businessEmail;
    private String businessDescription;
    private String businessPhone;
    private String licenseNumber;
    private String businessLogo;
    private  String spCode;
    private List<ValueChainResponse> valueChains;
//    private ExpertiseResponse expertise;
    private Boolean available;

    @Embedded
    private WorkingHoursDTO workingHours;
    private Set<DayOfWeek> workingDays;
    @Embedded
    private CordinatesDTO businessLocation;
    private Boolean businessVerified;
    private BranchResponse branch;
    private String profilePicture;
    private Double averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
