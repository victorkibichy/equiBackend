package com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.ProductCategory;
import com.EquiFarm.EquiFarm.ServiceProvider.Expertise.Expertise;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices.TypeOfServices;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.QualityCheck;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeExclude;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "provided_services")
public class ProvidedServices extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false)
    private Long id;
    private Double price;
    private String description;
    private String location;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(referencedColumnName = "id", name = "type_of_service_id")
//    private TypeOfServices typeOfServices;


//    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinTable(name = "type_of_services_provided", joinColumns = @JoinColumn(name = "provided_services_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "type_of_service_id", referencedColumnName = "id"))
//    private List<TypeOfServices> typeOfServices;

    @ManyToOne
    @JoinColumn(name = "type_of_service_id")
    private TypeOfServices typeOfServices;

    @ManyToOne
    @JoinColumn(name = "service_provider_id")
    private ServiceProvider serviceProvider;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(referencedColumnName = "id", name = "type_of_service_id")
//    private TypeOfServices typeOfServices;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(referencedColumnName = "id", name = "expertise_id")
//    private Expertise expertise;
}
