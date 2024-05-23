package com.EquiFarm.EquiFarm.Warehouse;

import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;

import com.EquiFarm.EquiFarm.ServiceProvider.WorkingHours;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.user.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@Table(name = "Warehouse")
public class Warehouse extends Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false)
    private Long id;
    private String warehouseName;
    private String warehousePhoneNumber;
    private String warehouseEmail;
    private String description;
    private Boolean isAvailable;
    private Boolean warehouseVerified;
    private String warehouseDescription;
    @Column(length = 16777215)
    private String warehouseLogo;
    private String licenceNumber;
    //TODO:Set up the following fields
    private Double capacity;
    private Double unitPrice;
    private String unitMeasurement;
    private Double leasePeriod;
    private Double averageRating;

    @Column(name = "Vacant", columnDefinition = "boolean default true")
    @Builder.Default
    private boolean vacant = true;

    @Column(name = "warehouse_location")
    @Embedded
    private Cordinates warehouseLocation;
    private String warehouseCode;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "warehouse_chains", joinColumns = @JoinColumn(name = "warehouse_id"),
            inverseJoinColumns = @JoinColumn(name = "value_chain_id"))
    private List<ValueChain> valueChains;
}

