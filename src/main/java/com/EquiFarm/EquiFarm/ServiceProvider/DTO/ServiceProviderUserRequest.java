package com.EquiFarm.EquiFarm.ServiceProvider.DTO;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import com.EquiFarm.EquiFarm.ValueChain.DTO.ValueChainRequest;
import com.EquiFarm.EquiFarm.ValueChain.DTO.ValueChainResponse;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.utils.Gender;

import jakarta.persistence.Embedded;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProviderUserRequest {
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
    private String businessName;
    private String businessEmail;
    private String businessDescription;
    private String businessPhone;
    private String licenseNumber;
    private String businessLogo;
    private List<Long> valueChainIds;
//    private Long valueChainId;
//    private Long expertiseId;
    @Embedded
    private WorkingHoursDTO workingHours;
    private Set<DayOfWeek> workingDays;
    @Embedded
    private CordinatesDTO businessLocation;
    private Boolean businessVerified;
    private String profilePicture;
}
