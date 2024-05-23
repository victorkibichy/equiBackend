package com.EquiFarm.EquiFarm.ServiceProvider;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cordinates {
    @Column(name = "latitude")
    @Builder.Default
    private Double latitude = -0.0236;

    @Column(name = "longitude")
    @Builder.Default
    private Double longitude = 37.9062;
}
