package com.EquiFarm.EquiFarm.AgriBusiness.TypeOfBusiness;

import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Type_Of_Business")
public class TypeOfBusiness extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "typeOfBusiness", nullable = false, length = 126, unique = true)
    private String typeOfBusiness;

    @Column(name = "description", nullable = true)
    @Size(max = 250)
    @Builder.Default
    private String description = "No description available";
}
