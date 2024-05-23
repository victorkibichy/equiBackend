package com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices;

import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.DTO.ProvidedServicesRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/serviceProvider/provided/services")
@Tag(name = "Service Provider Services")
@RequiredArgsConstructor
public class ProvidedServicesController {
    private final ProvidedServicesService providedServicesService;

    @PostMapping("/create/{typeOfServiceId}")
    @PreAuthorize("hasAuthority('serviceProvider:create')")
    public ResponseEntity<?> create(@PathVariable("typeOfServiceId") Long typeOfServiceId,
                                     @RequestBody ProvidedServicesRequest providedServicesRequest){
        return ResponseEntity.ok(providedServicesService.createProvidedServices(typeOfServiceId, providedServicesRequest));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(@RequestParam(required = false) Long serviceproviderId){
        return ResponseEntity.ok(providedServicesService.fetchAll(serviceproviderId));
    }

    @GetMapping("/get/by/id{providedServiceId}")
    public ResponseEntity<?> fetchById(@PathVariable("providedServiceId") Long providedServiceId){
        return ResponseEntity.ok(providedServicesService.fetchById(providedServiceId));
    }

//    @GetMapping("/get/by/service/provider/{serviceProviderId}")
//    public ResponseEntity<?> fetchByServiceProvider(@PathVariable("serviceProviderId") Long serviceProviderId){
//        return ResponseEntity.ok(providedServicesService.fetchProviderServices(serviceProviderId));
//    }

    @PutMapping("update/{providedServiceId}")
    @PreAuthorize("hasAuthority('serviceProvider:update')")
    public ResponseEntity<?> updateService(@PathVariable("providedServiceId") Long providedServiceId,
                                           @RequestBody ProvidedServicesRequest providedServicesRequest){
        return ResponseEntity.ok(providedServicesService.updateProvidedService(providedServiceId, providedServicesRequest));
    }
    @DeleteMapping("/delete/{providedServiceId}")
    @PreAuthorize("hasAuthority('admin:;delete')")
    public ResponseEntity<?> deleteService(@PathVariable("serviceProviderId") Long providedServiceId){
        return ResponseEntity.ok(providedServicesService.deleteService(providedServiceId));
    }
}
