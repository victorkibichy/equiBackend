package com.EquiFarm.EquiFarm.Farmer;

import com.EquiFarm.EquiFarm.Branch.Branch;
import com.EquiFarm.EquiFarm.Staff.Staff;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.user.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Farmers")
public class Farmer extends Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Builder.Default
    @Column(name = "latitude", nullable = true)
    private Double latitude = -0.0236;

    @Builder.Default
    @Column(name = "longitude", nullable = true)
    private Double longitude = 37.9062;

    @Column(name = "average_rating")
    private Double averageRating;

    private String farmerCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private TypeOfFarming typeOfFarming;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "value_chain_id", referencedColumnName = "id")
//    private ValueChain valueChain;



//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "farmer_chains", joinColumns = @JoinColumn(name = "farmer_id"),
//            inverseJoinColumns = @JoinColumn(name = "value_chain_id"))
//    private List<ValueChain> valueChains;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "farmer_chains",
            joinColumns = @JoinColumn(name = "farmer_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "value_chain_id", referencedColumnName = "id"))
    private List<ValueChain> valueChains;

//    @ManyToMany(mappedBy = "farmerList", fetch = FetchType.LAZY)
//    private List<ValueChain> valueChains;

    @Column(name = "verified", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isVerified = false;

    //Assign FIELD_OFFICER to farmer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff fieldOfficer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

}
