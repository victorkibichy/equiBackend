package com.EquiFarm.EquiFarm.Manufacturer.SubCategories;

import com.EquiFarm.EquiFarm.Manufacturer.Categories.Category;
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
@Table(name = "manufacturer_sub_category")
public class SubCategory extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "sub_category", nullable = false, length = 126)
    private String subCategory;

    @Column(name = "description", nullable = true)
    @Size(max = 250)
    @Builder.Default
    private String description = "No description available";

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
}
