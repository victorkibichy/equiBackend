package com.EquiFarm.EquiFarm.ServiceProvider.ServiceRequest.DTO;

import com.EquiFarm.EquiFarm.Farmer.DTO.FarmersResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DisplayImages;
import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;
import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.DTO.ProvidedServicesResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestResponse {
    private Long id;
    private String description;
    private Double budget;
    private Cordinates coordinates;
    private LocalDate preferredDate;
    private List<DisplayImages> displayImages;
//    private FarmersResponse farmer;
//    private ProvidedServicesResponse providedServices;
}
