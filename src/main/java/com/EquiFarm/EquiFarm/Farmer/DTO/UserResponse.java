package com.EquiFarm.EquiFarm.Farmer.DTO;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private Boolean isStaff;
    private Boolean isAdmin;
    private String role;
    private boolean active;
    private Double latitude;
    private Double longitude;
    private Double averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
