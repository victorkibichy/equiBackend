package com.EquiFarm.EquiFarm.FarmTech.DTO;

import com.EquiFarm.EquiFarm.ServiceProvider.DTO.CordinatesDTO;
import com.EquiFarm.EquiFarm.ServiceProvider.WorkingHours;
import com.EquiFarm.EquiFarm.Staff.Staff;
import com.EquiFarm.EquiFarm.utils.Gender;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmTechRequest {
    private String firstName;
    @NotNull(message = "Last name is required.")
    private String lastName;
    @Email(message = "Invalid email address.")
    private String email;
    @Pattern(regexp = "^\\d{10}$", message = "Invalid mobile number entered")
    private String phoneNo;
    private String idNumber;
    private String bio;
    private Gender gender;
    private String licenceNumber;
    private String farmTechName;
    private String farmTechEmail;
    private String farmTechDescription;
    private String farmTechPhone;
    private String licenseNumber;
    private String farmTechLogo;
    @Embedded
    private WorkingHours workingHours;
    private Set<DayOfWeek> workingDays;
    @Embedded
    private CordinatesDTO farmTechLocation;
    private Boolean farmTechVerified;
    private String profilePicture;
    private List<Long> valueChainIds;
}
