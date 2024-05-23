package com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory;

import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.ProcessorCategory;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "processor_sub_category")
public class ProcessorSubCategory extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "processor_sub_category", nullable = false, length = 126)
    private String processorSubCategory;

    @Column(name = "description", nullable = true)
    @Size(max = 250)
    @Builder.Default
    private String description = "No description available";

    @ManyToOne(fetch = FetchType.LAZY)
    private ProcessorCategory processorCategory;
}

