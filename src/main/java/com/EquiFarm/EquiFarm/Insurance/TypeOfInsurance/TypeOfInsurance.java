package com.EquiFarm.EquiFarm.Insurance.TypeOfInsurance;

import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Types_of_Insurance")
public class TypeOfInsurance extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "type_of_insurance", nullable = false, length = 120)
    private String typeOfInsurance;

    @Column(nullable = true, length = 250)
    private String description;
}
