package com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.DTO;

import com.EquiFarm.EquiFarm.ServiceProvider.DTO.ServiceProviderUserResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.Expertise.DTO.ExpertiseResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices.DTO.TypeOfServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvidedServicesResponse {
    private Long id;
    private Double price;
    private String description;
    private String location;
    private TypeOfServiceResponse typeOfServices;

//    private ExpertiseResponse expertise;
//    private ServiceProviderUserResponse serviceProvider;
}
