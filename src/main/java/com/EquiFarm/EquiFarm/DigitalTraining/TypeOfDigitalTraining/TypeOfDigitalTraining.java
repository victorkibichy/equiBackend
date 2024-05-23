package com.EquiFarm.EquiFarm.DigitalTraining.TypeOfDigitalTraining;

import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(callSuper=false)
@Table(name = "Type_of_Digital_Training")
public class TypeOfDigitalTraining extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "type_of_digital_training", nullable = false, length = 120)
    private String typeOfDigitalTraining;

    @Column(nullable = true, length = 250)
    private String description;

}
