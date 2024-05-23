package com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices;

import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.ProvidedServices;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Type_Of_Services")
public class TypeOfServices extends TrackingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "typeOfService", nullable = false, length = 126, unique = true)
    private String typeOfService;

    @Column(name = "description", nullable = true)
    @Size(max = 250)
    @Builder.Default
    private String description = "No description available";
    @Enumerated(EnumType.STRING)
    private ServicesCategory servicesCategory;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "expertise_id", referencedColumnName = "id")
//    private Expertise expertise;

    @OneToMany(mappedBy = "typeOfServices")
    private List<ProvidedServices> providedServices;
}
