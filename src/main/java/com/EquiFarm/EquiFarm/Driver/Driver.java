package com.EquiFarm.EquiFarm.Driver;

import com.EquiFarm.EquiFarm.Branch.Branch;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.user.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Driver")
public class Driver extends Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "latitude", nullable = true)
    @Builder.Default
    private Double latitude = -0.0236;

    @Column(name = "longitude", nullable = true)
    @Builder.Default
    private Double longitude = 37.9062;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ModeOfTransport modeOfTransport;

    @Column(name = "available", columnDefinition = "boolean default true")
    @Builder.Default
    private boolean isAvailable = true;

    @Column(name = "number_plate", length = 20, nullable = true)
    private String numberPlate;

    @Lob
    @Column(name = "vehicle_image")
    private String vehicleImage;

    @Column(name = "licence_number")
    private String licenceNumber;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "driver_code")
    private String driverCode;

    @Column(name = "verified", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isVerified = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "driver_chains", joinColumns = @JoinColumn(name = "driver_id"),
            inverseJoinColumns = @JoinColumn(name = "value_chain_id"))
    private List<ValueChain> valueChains;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_d")
    private Branch branch;
}
