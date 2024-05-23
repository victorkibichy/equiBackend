package com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvidedServicesRequest {
    private Double price;
    private String description;
    private String location;

}
