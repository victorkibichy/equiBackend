package com.EquiFarm.EquiFarm.AgriBusiness;

import com.EquiFarm.EquiFarm.AgriBusiness.TypeOfBusiness.TypeOfBusiness;
import com.EquiFarm.EquiFarm.Branch.Branch;
import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;
import com.EquiFarm.EquiFarm.ServiceProvider.WorkingHours;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.user.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AgriBusiness")
public class AgriBusiness extends Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "typeOfBusiness_id")
    private TypeOfBusiness typeOfBusiness;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "business_email")
    private String businessEmail;

    @Column(name = "business_description")
    private String businessDescription;

    @Column(name = "business_phone")
    private String businessPhone;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "business_logo", length = 16777215)
    private String businessLogo;

    @Column(name = "working_hours")
    @Embedded
    private WorkingHours workingHours;

    @Column(name = "working_days")
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> workingDays;

     @Column(name = "business_location")
    @Embedded
    private Cordinates businessLocation;

    @Column(name = "verified")
    private Boolean businessVerified;

    @Column(name = "agribusiness_code")
    private String agribusinessCode;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "agribusiness_chains",
            joinColumns = @JoinColumn(name = "agribusiness_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "value_chain_id", referencedColumnName = "id"))
    private List<ValueChain> valueChains;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;
}
