package com.EquiFarm.EquiFarm.Processor.DTO;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.DTO.ProcessorCategoryResponse;
import com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory.DTO.ProcessorSubCategoryResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.CordinatesDTO;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.WorkingHoursDTO;
import com.EquiFarm.EquiFarm.utils.Gender;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessorResponse {
    private Long id;
    private UserResponse user;
    private String idNumber;
    private String bio;
    private Gender gender;
    private String licenceNumber;
    private String processorName;
    private String processorEmail;
    private String processorPhoneNumber;
    private Boolean processorVerified;
    private String processorCode;
    private String profilePicture;
    private ProcessorSubCategoryResponse processorSubCategory;
    private ProcessorCategoryResponse processorCategory;
    @Embedded
    private WorkingHoursDTO workingHours;
    private Set<DayOfWeek> workingDays;
    @Embedded
    private CordinatesDTO processorLocation;

}