package com.EquiFarm.EquiFarm.ServiceProvider.ServiceRequest.DTO;

import com.EquiFarm.EquiFarm.Farmer.DTO.FarmersResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DTO.DisplayImagesRequest;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DisplayImages;
import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;
import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.DTO.ProvidedServicesResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.ProvidedServices;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceRequest.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestRequest {
    private String Description;
    private Double Budget;
    private Cordinates coordinates;
    private LocalDate preferredDate;
    private List<DisplayImagesRequest> displayImages;
}
