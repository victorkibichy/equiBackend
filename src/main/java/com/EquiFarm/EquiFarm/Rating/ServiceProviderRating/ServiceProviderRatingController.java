package com.EquiFarm.EquiFarm.Rating.ServiceProviderRating;

import com.EquiFarm.EquiFarm.Rating.ServiceProviderRating.DTO.ServiceProviderRatingRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/serviceProviderRating")
@Tag(name = "Service Provider Rating")
@RequiredArgsConstructor
public class ServiceProviderRatingController {
    private final ServiceProviderRatingService serviceProviderRatingService;

    @PostMapping("/create")
    public ResponseEntity<?> createServiceProviderRating(@RequestBody ServiceProviderRatingRequest
                                                                     serviceProviderRatingRequest) {
        return ResponseEntity.ok(serviceProviderRatingService.createServiceProviderRating(serviceProviderRatingRequest));
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllServiceProviderRating() {
        return ResponseEntity.ok(serviceProviderRatingService.getAllServiceProviderRating());
    }

    @GetMapping("/get/by/serviceProviderRatingByServiceProviderId/{serviceProviderId}")
    public ResponseEntity<?> getServiceProviderRatingByServiceProviderId(@PathVariable("serviceProviderId") Long id) {
        return ResponseEntity.ok(serviceProviderRatingService.getServiceProviderRatingByServiceProviderId(id));
    }

    @GetMapping("/get/by/serviceProviderRatingByUserId/{userId}")
    public ResponseEntity<?> getServiceProviderRatingByUserId(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(serviceProviderRatingService.getServiceProviderRatingByUserId(id));
    }
    @DeleteMapping("/delete/{serviceProviderRatingId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteServiceProviderRating(@PathVariable("serviceProviderRatingId") Long id) {
        return ResponseEntity.ok(serviceProviderRatingService.deleteServiceProviderRating(id));
    }
}
