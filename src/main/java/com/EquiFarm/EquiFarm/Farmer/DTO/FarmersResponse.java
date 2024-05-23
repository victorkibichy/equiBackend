package com.EquiFarm.EquiFarm.Farmer.DTO;

import com.EquiFarm.EquiFarm.Branch.DTO.BranchResponse;
import com.EquiFarm.EquiFarm.Farmer.TypeOfFarming;
import com.EquiFarm.EquiFarm.Staff.DTO.StaffUserResponse;
import com.EquiFarm.EquiFarm.ValueChain.DTO.ValueChainResponse;
import com.EquiFarm.EquiFarm.utils.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmersResponse {
    private Long id;
    private UserResponse user;
    private String idNumber;
    private TypeOfFarming typeOfFarming;
    private Double latitude;
    private Double longitude;
    private Gender gender;
    private String bio;
    private  String farmerCode;
    private String profilePicture;
    private Double averageRating;
    private List<ValueChainResponse> valueChains;
    private Boolean isVerified;
    private StaffUserResponse fieldOfficer;
    private BranchResponse branch;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


