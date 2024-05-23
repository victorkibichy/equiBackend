package com.EquiFarm.EquiFarm.Branch;

import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(callSuper=false)
@Table(name = "Branches")
public class Branch extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "branch_sol", nullable = true)
    private String solId;

    @Column(name = "branch_name", nullable = true)
    private String branchName;

    @Column(name = "region", columnDefinition = "TEXT", nullable = true)
    @Size(max = 250)
    private String region;
}
