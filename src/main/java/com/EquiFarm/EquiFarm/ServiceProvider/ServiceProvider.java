package com.EquiFarm.EquiFarm.ServiceProvider;

import com.EquiFarm.EquiFarm.Branch.Branch;
import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.ProvidedServices;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.user.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Service_providers")
public class ServiceProvider extends Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

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

    @Column(name = "average_rating")
    @Builder.Default
    private Double averageRating = 0.0;

    private Boolean available;

    private String spCode;
    @Enumerated(EnumType.STRING)

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "value_chain_id", referencedColumnName = "id")
//    private ValueChain valueChain;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "expertise_id", referencedColumnName = "id")
//    private Expertise expertise;

    @OneToMany(mappedBy = "serviceProvider")
    private List<ProvidedServices> providedServices;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "service_provider_chains", joinColumns = @JoinColumn(name = "service_provider_id"),
            inverseJoinColumns = @JoinColumn(name = "value_chain_id"))
    private List<ValueChain> valueChains;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;
//@ManyToMany(mappedBy = "serviceProviderList", fetch = FetchType.LAZY)
//private List<ValueChain> valueChains;
}
