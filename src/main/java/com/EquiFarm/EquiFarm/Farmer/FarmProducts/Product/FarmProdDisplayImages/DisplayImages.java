package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages;

import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Farm_Products_Display_Images")
public class DisplayImages extends TrackingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Lob
    @Column(name = "display_image",nullable = false,length = 16777215)
    private String displayImage;
}
