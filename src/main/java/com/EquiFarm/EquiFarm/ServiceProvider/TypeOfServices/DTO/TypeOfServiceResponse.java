package com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices.DTO;

import com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices.ServicesCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeOfServiceResponse {
    private Long id;
    private String typeOfService;
    private String description;
    private ServicesCategory servicesCategory;
//    private ExpertiseResponse expertise;
}
