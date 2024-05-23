package com.EquiFarm.EquiFarm.ServiceProvider.ServiceRequest;

import com.EquiFarm.EquiFarm.ServiceProvider.ServiceRequest.DTO.ServiceRequestRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/serviceProvider/ServiceRequest")
@Tag(name = "Service Provider Requests")
@RequiredArgsConstructor
public class ServiceRequestController {
    private final ServiceRequestService serviceRequestService;
    @PostMapping("/new/{providedServiceId}")
    @PreAuthorize("hasAuthority('farmer:create')")
    public ResponseEntity<?>create(@PathVariable("providedServiceId") Long providedServiceId,
                                   @RequestBody ServiceRequestRequest serviceRequestRequest){
        return ResponseEntity.ok(serviceRequestService.makeServiceRequest(providedServiceId, serviceRequestRequest));
    }
    @GetMapping("/get/all")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(serviceRequestService.fetchAll());
    }

    @GetMapping("/get/by/service/provider/{id}")
    public ResponseEntity<?> getByServiceProvider(@PathVariable("id") Long id){
        return ResponseEntity.ok(serviceRequestService.findByServiceProvider(id));
    }

    @GetMapping("/get/by/id/{serviceRequestId}")
    public ResponseEntity<?> getById(@PathVariable("serviceRequestId") Long serviceRequestId){
        return ResponseEntity.ok(serviceRequestService.getServiceRequestById(serviceRequestId));
    }

    @PutMapping("/update/{serviceRequestId}")
    @PreAuthorize("hasAuthority('farmer:update')")
    public ResponseEntity<?> updateServiceRequest(@PathVariable("serviceRequestId") Long serviceRequestId,
                                                  @RequestBody ServiceRequestRequest serviceRequestRequest){
        return ResponseEntity.ok(serviceRequestService.updateServiceRequest(serviceRequestId, serviceRequestRequest));
    }

    @DeleteMapping("/delete/{serviceRequestId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteServiceRequest(@PathVariable("serviceRequestId") Long serviceRequestId){
        return ResponseEntity.ok(serviceRequestService.deleteServiceRequest(serviceRequestId));
    }
}