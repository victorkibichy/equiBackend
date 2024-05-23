package com.EquiFarm.EquiFarm.ServiceProvider;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.EquiFarm.EquiFarm.ServiceProvider.DTO.ServiceProviderUserRequest;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.ServiceProviderUserRequest.ServiceProviderUserRequestBuilder;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/serviceProvider")
@Tag(name = "Service Providers")
@RequiredArgsConstructor
public class ServiceProviderController {
    private final ServiceProviderService serviceProviderService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('serviceProvider:read')")
    public ResponseEntity<?> getServiceProviderProfile() {
        return ResponseEntity.ok(serviceProviderService.getServiceProviderProfile());
    }

    @GetMapping("/get/by/serviceProviderId/{serviceProviderId}")
    public ResponseEntity<?> getServiceProviderById(@PathVariable("serviceProviderId") Long id) {
        return ResponseEntity.ok(serviceProviderService.getServiceProviderByProfileId(id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllServiceProviders() {
        return ResponseEntity.ok(serviceProviderService.getAllServiceProviders());
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('serviceProvider:update')")
    public ResponseEntity<?> serviceProviderProfileUpdate(
            @RequestBody ServiceProviderUserRequest serviceProviderUserRequest) {
        return ResponseEntity.ok(serviceProviderService.serviceProviderProfileUpdate(serviceProviderUserRequest));
    }

    @GetMapping("/get/by/value/chain/{valueChainId}")
    public ResponseEntity<?> fetchServiceProvidersByValueChain(@PathVariable("valueChainId") Long valueChainId){
        return ResponseEntity.ok(serviceProviderService.getAllServiceProvidersInValueChain(valueChainId));
    }

    @PutMapping("/update/by/serviceProviderId/{serviceProviderId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateServiceProvider(@RequestBody ServiceProviderUserRequest serviceProviderUserRequest,
            @PathVariable("serviceProviderId") Long id) {
        return ResponseEntity.ok(serviceProviderService.updateServiceProviderById(serviceProviderUserRequest, id));

    }
    @PutMapping("/update/by/userId/{userId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateServiceProviderByUserId(@RequestBody ServiceProviderUserRequest serviceProviderUserRequest,
                                                   @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(serviceProviderService.updateServiceProviderByUserId(userId, serviceProviderUserRequest));
    }
    @GetMapping("/get/by/userId/{userId}")
    public ResponseEntity<?> fetchByUserId(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(serviceProviderService.fetchServiceProviderByUserId(userId));
    }

    @DeleteMapping("/delete/{serviceProviderId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteServiceProvider(
            @PathVariable("serviceProviderId") Long id) {
        return ResponseEntity.ok(serviceProviderService.serviceProviderDelete(id));
    }

}
