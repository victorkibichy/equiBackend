package com.EquiFarm.EquiFarm.Customer.DTO;

import com.EquiFarm.EquiFarm.Branch.DTO.BranchResponse;
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
public class CustomerResponse {
    private Long id;
    private UserResponse user;
    private String idNumber;
    private Gender gender;
    private String bio;
    private Double longitude;
    private Double latitude;
    private BranchResponse branch;
    private String profilePicture;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
