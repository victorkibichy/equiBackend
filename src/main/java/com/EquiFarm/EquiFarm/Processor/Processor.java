package com.EquiFarm.EquiFarm.Processor;

import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.ProcessorCategory;
import com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory.ProcessorSubCategory;
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
@Table(name = "processors")
public class Processor extends Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;
    private String licenceNumber;
    private String processorName;
    private String processorEmail;
    private String processorPhoneNumber;
    private String processorDescription;
    @Column(length = 16777215)
    private String processorLogo;
    private Boolean processorVerified;
    private Double averageRating;
    private String processorCode;

    @Column(name = "working_hours")
    @Embedded
    private WorkingHours workingHours;

    @Column(name = "working_days")
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> workingDays;

    @Column(name = "processor_location")
    @Embedded
    private Cordinates processorLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processor_category_id")
    private ProcessorCategory processorCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processor_sub_category_id")
    private ProcessorSubCategory processorSubCategory;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "processor_chains", joinColumns = @JoinColumn(name = "processor_id"),
            inverseJoinColumns = @JoinColumn(name = "value_chain_id"))
    private List<ValueChain> valueChains;
}


