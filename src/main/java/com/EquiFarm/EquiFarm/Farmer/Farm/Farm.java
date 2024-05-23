package com.EquiFarm.EquiFarm.Farmer.Farm;

import java.util.ArrayList;
import java.util.List;

import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
// import com.vividsolutions.jts.geom.MultiPolygon;
// import org.hibernate.annotations.Type;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Farm")
public class Farm extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "farmer_id")
    @Builder.Default
    private List<Farmer> farmers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private FarmOwnership typeOfOwnership;
    // @Column(columnDefinition = "geometry(MultiPolygon,4326)", nullable = true)
    // @Type(type = "org.hibernate.spatial.GeometryType")
    // private MultiPolygon farmShape;
    @Column(name = "title_number", length = 156, nullable = true)
    private String titleNumber;
    @Lob
    @Column(name = "title_deed", nullable = true)
    private String titleDeed;
    @Column(name = "farm_size", length = 156, nullable = true)
    private String farmSize;
    @Column(name = "latitude", nullable = true)
    @Builder.Default
    private Double latitude = -0.0236;
    @Column(name = "longitude", nullable = true)
    @Builder.Default
    private Double longitude = 37.9062;
    @Column(name = "farm_income", nullable = true)
    @Builder.Default
    private Double farmIncome = 0.00;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private SoilType soilType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)

    private SourceOfWater sourceOfWater;
    @Column(nullable = true, length = 250)
    private String farmHistory;

    // crop_types
    // types of liveStocks
    // farm_practices
}
