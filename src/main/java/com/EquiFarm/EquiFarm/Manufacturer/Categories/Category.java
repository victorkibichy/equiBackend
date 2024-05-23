package com.EquiFarm.EquiFarm.Manufacturer.Categories;

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
@Table(name = "manufacturer_category")
public class Category extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "category", nullable = false, length = 126)
    private String category;

    @Column(name = "description", nullable = true)
    @Size(max = 250)
    @Builder.Default
    private String description = "No description available";
}
