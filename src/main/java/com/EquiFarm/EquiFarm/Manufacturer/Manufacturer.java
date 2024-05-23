package com.EquiFarm.EquiFarm.Manufacturer;

import com.EquiFarm.EquiFarm.Manufacturer.Categories.Category;
import com.EquiFarm.EquiFarm.Manufacturer.SubCategories.SubCategory;
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
@Table(name = "manufacturers")
public class Manufacturer extends Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;
    private String licenceNumber;
    private String manufacturerName;
    private String manufacturerEmail;
    private String manufacturerPhoneNumber;
    private String manufacturerDescription;
    @Column(length = 16777215)
    private String manufacturerLogo;
    private Boolean manufacturerVerified;
    private Double averageRating;
    private String manufacturerCode;

    @Column(name = "working_hours")
    @Embedded
    private WorkingHours workingHours;

    @Column(name = "working_days")
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> workingDays;

    @Column(name = "manufacturer_location")
    @Embedded
    private Cordinates manufacturerLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "manufacturer_chains", joinColumns = @JoinColumn(name = "manufacturer_id"),
            inverseJoinColumns = @JoinColumn(name = "value_chain_id"))
    private List<ValueChain> valueChains;
}
