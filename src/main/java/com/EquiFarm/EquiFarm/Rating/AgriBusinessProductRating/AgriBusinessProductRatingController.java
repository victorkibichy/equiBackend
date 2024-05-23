package com.EquiFarm.EquiFarm.Rating.AgriBusinessProductRating;

import com.EquiFarm.EquiFarm.Rating.AgriBusinessProductRating.DTO.AgriBusinessProductRatingRequest;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/agriBusinessProductRating")
@Tag(name = "AgriBusiness Product Rating")
@RequiredArgsConstructor
public class AgriBusinessProductRatingController {
    private final AgriBusinessProductRatingService agriBusinessProductRatingService;


    @PostMapping("/create")
    public ResponseEntity<?> createAgriBusinessProductRating(@RequestBody AgriBusinessProductRatingRequest agriBusinessProductRatingRequest) {
        return ResponseEntity.ok(agriBusinessProductRatingService.createAgriBusinessProductRating(agriBusinessProductRatingRequest));
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllAgriBusinessProductRating() {
        return ResponseEntity.ok(agriBusinessProductRatingService.getAllAgriBusinessProductRatings());

    }
    @GetMapping("/get/by/agriBusinessProductRatingByDriverId/{agriBusinessProductId}")
    public ResponseEntity<?> getAgriBusinessProductRatingByAgriBusinessProductId(@PathVariable("agriBusinessProductId") Long id) {
        return ResponseEntity.ok(agriBusinessProductRatingService.getAgriBusinessProductRatingByAgriBusinessProductId(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getAgriBusinessProductRatingsByUserId(@PathVariable Long userId) {
        ApiResponse<?> response = agriBusinessProductRatingService.getAgriBusinessProductRatingsByUserId(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @DeleteMapping("/delete/{agriBusinessProductRatingId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteAgriBusinessProductRating(
            @PathVariable("agriBusinessProductRatingId") Long id) {
        return ResponseEntity.ok(agriBusinessProductRatingService.deleteAgriBusinessProductRating(id));
    }
}