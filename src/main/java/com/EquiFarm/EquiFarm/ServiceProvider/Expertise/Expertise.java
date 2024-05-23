package com.EquiFarm.EquiFarm.ServiceProvider.Expertise;

import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Service_provider_expertise")
public class Expertise extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "expertise_name", nullable = false, length = 126, unique = true)
    private String expertiseName;

    @Column(name = "description", nullable = true)
    @Size(max = 250)
    private String Description;
}
