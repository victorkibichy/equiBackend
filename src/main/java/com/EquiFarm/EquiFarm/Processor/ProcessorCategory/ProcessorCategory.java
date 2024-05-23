package com.EquiFarm.EquiFarm.Processor.ProcessorCategory;

import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "processor_category")
public class ProcessorCategory extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "processor_category", nullable = false, length = 126)
    private String processorCategory;

    @Column(name = "description", nullable = true)
    @Size(max = 250)
    @Builder.Default
    private String description = "No description available";
}
