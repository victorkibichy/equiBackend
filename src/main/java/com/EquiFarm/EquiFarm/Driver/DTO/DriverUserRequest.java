package com.EquiFarm.EquiFarm.Driver.DTO;

import com.EquiFarm.EquiFarm.Driver.ModeOfTransport;
import com.EquiFarm.EquiFarm.utils.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverUserRequest {
    private String firstName;
    @NotNull(message = "Last name is required.")
    private String lastName;
    @Email(message = "Invalid email address.")
    private String email;
    @Pattern(regexp = "^\\d{10}$", message = "Invalid mobile number entered")
    private String phoneNo;
    private Double latitude;
    private Double longitude;
    private Gender gender;
    private ModeOfTransport modeOfTransport;
    private boolean isAvailable;
    private String numberPlate;
    private String idNumber;
    private String vehicleImage;
    private String licenceNumber;
    private String bio;
    private String profilePicture;
    private Boolean isVerified;
    private List<Long> valueChainIds;
}
