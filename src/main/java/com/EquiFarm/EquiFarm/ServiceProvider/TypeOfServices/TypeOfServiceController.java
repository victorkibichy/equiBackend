package com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices.DTO.TypeOfServiceRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/type/of/service")
@Tag(name = "Type of Services", description = "This include, soil testers and veterinary officers")
@RequiredArgsConstructor
public class TypeOfServiceController {
    private final TypeOfServicesService typeOfServicesService;

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllTypeOfServices() {
        return ResponseEntity.ok(typeOfServicesService.getAllTypesOfServices());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> createTypeOfService(@RequestBody TypeOfServiceRequest typeOfServiceRequest) {
        return ResponseEntity.ok(typeOfServicesService.createTypeOfService(typeOfServiceRequest));
    }

    @PutMapping("/update/{typeOfServiceId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateTypeOfService(@PathVariable("typeOfServiceId") Long id,
            @RequestBody TypeOfServiceRequest typeOfServiceRequest) {
        return ResponseEntity.ok(typeOfServicesService.updateTypeOfService(typeOfServiceRequest, id));
    }

    @GetMapping("/get/by/typeOfServiceById/{typeOfServiceId}")
    public ResponseEntity<?> getTypeOfServiceByTypeOfServiceId(@PathVariable("typeOfServiceId") Long id){
        return ResponseEntity.ok(typeOfServicesService.getTypeOfServiceById(id));
    }

    @DeleteMapping("/delete/{typeOfServiceId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteTypeOfServiceByTypeOfServiceId(@PathVariable("typeOfServiceId") Long id){
        return ResponseEntity.ok(typeOfServicesService.typeOfServiceDelete(id));
    }
}
