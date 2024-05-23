package com.EquiFarm.EquiFarm.Admin.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.EquiFarm.EquiFarm.utils.Gender;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserRequest {
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
    private String profilePicture;
}