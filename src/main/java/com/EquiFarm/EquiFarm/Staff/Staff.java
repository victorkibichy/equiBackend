
package com.EquiFarm.EquiFarm.Staff;

import com.EquiFarm.EquiFarm.user.Profile;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "Staff")
public class Staff extends Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private StaffRole staffRole = StaffRole.UNCLASSIFIED_STAFF;


    @Column(name = "available", columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean available = true;
}
