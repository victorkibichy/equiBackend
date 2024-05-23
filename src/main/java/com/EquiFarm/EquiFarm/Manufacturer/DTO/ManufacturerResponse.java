package com.EquiFarm.EquiFarm.Manufacturer.DTO;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import com.EquiFarm.EquiFarm.Manufacturer.Categories.DTO.CategoryResponse;
import com.EquiFarm.EquiFarm.Manufacturer.SubCategories.DTO.SubCategoryResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.CordinatesDTO;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.WorkingHoursDTO;
import com.EquiFarm.EquiFarm.ValueChain.DTO.ValueChainResponse;
import com.EquiFarm.EquiFarm.utils.Gender;
import jakarta.persistence.*;
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
public class ManufacturerResponse {
    private Long id;
    private UserResponse user;
    private String idNumber;
    private String bio;
    private Gender gender;
    private String licenceNumber;
    private String manufacturerName;
    private String manufacturerEmail;
    private String manufacturerPhoneNumber;
    private Boolean manufacturerVerified;
    private String manufacturerCode;
    private String profilePicture;
    private SubCategoryResponse subCategory;
    private CategoryResponse category;
    @Embedded
    private WorkingHoursDTO workingHours;
    private Set<DayOfWeek> workingDays;
    @Embedded
    private CordinatesDTO manufacturerLocation;
    private Double averageRating;
    private List<ValueChainResponse> valueChains;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
