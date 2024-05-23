package com.EquiFarm.EquiFarm.FarmTech;

import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;
import com.EquiFarm.EquiFarm.ServiceProvider.WorkingHours;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.user.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FarmTech")
public class FarmTech extends Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "farmtech_name")
    private String farmTechName;

    @Column(name = "farmtech_email")
    private String farmTechEmail;

    @Column(name = "farmtech_description")
    private String farmTechDescription;

    @Column(name = "farmtech_phone")
    private String farmTechPhone;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "farmtech_logo", length = 16777215)
    private String farmTechLogo;

    @Column(name = "working_hours")
    @Embedded
    private WorkingHours workingHours;

    @Column(name = "working_days")
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> workingDays;

    @Column(name = "farmtech_location")
    @Embedded
    private Cordinates farmTechLocation;

    @Column(name = "verified")
    private Boolean farmTechVerified;

    @Column(name = "farmtech_code")
    private String farmTechCode;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "farmtech_chains",
            joinColumns = @JoinColumn(name = "farmtech_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "value_chain_id", referencedColumnName = "id"))
    private List<ValueChain> valueChains;
}

