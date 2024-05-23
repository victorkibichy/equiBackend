package com.EquiFarm.EquiFarm.ValueChain;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusiness;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProduct;
import com.EquiFarm.EquiFarm.Driver.Driver;
import com.EquiFarm.EquiFarm.FarmTech.FarmTech;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Manufacturer.Manufacturer;
import com.EquiFarm.EquiFarm.Processor.Processor;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.Warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "value_chain")
public class ValueChain extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "value_chain", nullable = false, length = 120)
    private String valueChain;

    @Column(nullable = true, length = 250)
    private String description;
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "service_provider_chains", joinColumns = @JoinColumn(name = "service_provider_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "value_chain_id", referencedColumnName = "id"))
//    private List<ServiceProvider> serviceProviderList;
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "farmer_chains", joinColumns = @JoinColumn(name = "farmer_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "value_chain_id", referencedColumnName = "id"))
//    private List<Farmer> farmerList;

    @ManyToMany(mappedBy = "valueChains", fetch = FetchType.LAZY)
    private List<ServiceProvider> serviceProviderList;

    @ManyToMany(mappedBy = "valueChains", fetch = FetchType.LAZY)
    private List<Farmer> farmerList;

    @ManyToMany(mappedBy = "valueChains", fetch = FetchType.LAZY)
    private List<AgriBusiness> agriBusinessList;

    @ManyToMany(mappedBy = "valueChains", fetch = FetchType.LAZY)
    private List<Driver> driverList;

    @ManyToMany(mappedBy = "valueChains", fetch = FetchType.LAZY)
    private List<Manufacturer> manufacturerList;

    @ManyToMany(mappedBy = "valueChains", fetch = FetchType.LAZY)
    private List<Warehouse> warehouseList;

    @ManyToMany(mappedBy = "valueChains", fetch = FetchType.LAZY)
    private List<Processor> processorList;

    @ManyToMany(mappedBy = "valueChains",fetch = FetchType.LAZY)
    private List<FarmProducts> farmProductsList;

    @ManyToMany(mappedBy = "valueChains",fetch = FetchType.LAZY)
    private List<AgriBusinessProduct> agriBusinessProductsList;

    @ManyToMany(mappedBy = "valueChains",fetch = FetchType.LAZY)
    private List<FarmTech> farmTechList;

}
