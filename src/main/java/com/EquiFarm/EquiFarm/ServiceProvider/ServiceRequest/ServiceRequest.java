package com.EquiFarm.EquiFarm.ServiceProvider.ServiceRequest;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DisplayImages;
import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;
import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.ProvidedServices;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_request")
public class ServiceRequest  extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;
    private Double budget;
    private Cordinates coordinates;
    private LocalDate preferredDate;
    private RequestStatus requestStatus;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "service_request_display_images",
            joinColumns = @JoinColumn(name = "service_request_id"),
            inverseJoinColumns = @JoinColumn(name = "display_image_id"))
    @Builder.Default
    private List<DisplayImages> displayImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provided_services_id", referencedColumnName = "id")
    private ProvidedServices providedServices;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "service_provider_id", referencedColumnName = "id")
//    private ServiceProvider serviceProvider;
}
