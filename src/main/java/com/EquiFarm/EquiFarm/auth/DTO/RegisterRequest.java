//package com.EquiFarm.EquiFarm.auth.DTO;
//
//import javax.validation.constraints.Email;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Pattern;
//
//import com.EquiFarm.EquiFarm.user.Role;
//
//import jakarta.persistence.Column;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class RegisterRequest {
//  @NotNull(message = "First name is required.")
//  private String firstName;
//  @NotNull(message = "Last name is required.")
//  private String lastName;
//  @Email(message = "Invalid email address.")
//  private String email;
//  @NotNull(message = "Password is required.")
//  private String password;
//  @Pattern(regexp = "^\\d{10}$",message = "Invalid mobile number entered")
//  private String phoneNo;
//  private String nationalId;
//  private Role role;
//  @Column(name = "latitude", nullable = true)
//  private Double latitude = -0.0236;
//  @Column(name = "longitude", nullable = true)
//  private Double longitude = 37.9062;
//}
package com.EquiFarm.EquiFarm.auth.DTO;

import com.EquiFarm.EquiFarm.user.Role;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  @NotNull(message = "First name is required.")
  private String firstName;
  @NotNull(message = "Last name is required.")
  private String lastName;
  private String email;
//  @Email(message = "Invalid email address.")
//  @Null(message = "Either email or phone number must be provided.")
//  private String email;
//  @Pattern(regexp = "^\\d{10}$", message = "Invalid mobile number entered")
//  @Null(message = "Either email or phone number must be provided.")
//  private String phoneNo;
  @NotNull(message = "Password is required.")
  private String password;
  @Pattern(regexp = "^\\d{10}$",message = "Invalid mobile number entered")
  private String phoneNo;
  private String nationalId;
  private Role role;
  @Column(name = "latitude", nullable = true)
  private Double latitude = -0.0236;
  @Column(name = "longitude", nullable = true)
  private Double longitude = 37.9062;
}