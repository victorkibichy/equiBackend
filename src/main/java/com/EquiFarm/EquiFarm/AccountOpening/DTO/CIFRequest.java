package com.EquiFarm.EquiFarm.AccountOpening.DTO;

import com.EquiFarm.EquiFarm.AccountOpening.*;
import com.EquiFarm.EquiFarm.AccountOpening.Enums.*;
import com.EquiFarm.EquiFarm.utils.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CIFRequest {
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;
    private Boolean minor;
    private Gender gender;
    private String occupation;
    private String phoneNumber;
    private String email;
    private String prefName;
    private Salutation salutation;
    private NatureOfIncome natureOfIncome;
    private String nameOfEmployer;
    private MaritalStatus maritalStatus;
    private Nationality nationality;
    private EmploymentStatus employmentStatus;
    private Address address;
    private String nationalId;
}
