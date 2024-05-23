package com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices.DTO;

import javax.validation.constraints.NotNull;

import com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices.ServicesCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeOfServiceRequest {
    @NotNull(message = "Type of service is required.")
    private String typeOfService;
    private String description;
//    private Long expertiseId;
    private ServicesCategory servicesCategory;
}
