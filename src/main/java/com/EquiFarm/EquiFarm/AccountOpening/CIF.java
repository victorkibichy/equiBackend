package com.EquiFarm.EquiFarm.AccountOpening;

import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "CIF")
public class CIF extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String nationalId;
    private String Cifld;
    private String AcctNum;
    private String LienAmt;
    private String acid;
    private String crncyCode;
    private String address1;
    private String address2;
    private String phoneNumber2;
    private String code;
    private String refCode;
    private String schemeType;
    private String cntryCode;
    private String ClrBalamt;
    private String solld;
//    private LocalDate dateOfBirth;
//    private Boolean minor;
//    @Enumerated(EnumType.STRING)
//    private Gender gender;
//    private String occupation;
//    @Column(unique = true)
//    private String phoneNumber;
//    private String email;
//    private String prefName;
//    @Enumerated(EnumType.STRING)
//    private Salutation salutation;
//    @Enumerated(EnumType.STRING)
//    private NatureOfIncome natureOfIncome;
//    private String nameOfEmployer;
//    @Enumerated(EnumType.STRING)
//    private MaritalStatus maritalStatus;
//    private Nationality nationality;
//    @Enumerated(EnumType.STRING)
//    private EmploymentStatus employmentStatus;
//    @Embedded
//    private Address address;
//    @Column(unique = true)
}

