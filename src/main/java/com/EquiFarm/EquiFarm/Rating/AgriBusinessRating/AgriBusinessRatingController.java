package com.EquiFarm.EquiFarm.Rating.AgriBusinessRating;

import com.EquiFarm.EquiFarm.Rating.AgriBusinessRating.DTO.AgriBusinessRatingRequest;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/agriBusinessRating")
@Tag(name = "AgriBusiness Rating")
@RequiredArgsConstructor
public class AgriBusinessRatingController {
    private final AgriBusinessRatingService agriBusinessRatingService;

    @PostMapping("/create")
    public ResponseEntity<?> createAgriBusinessRating(@RequestBody AgriBusinessRatingRequest agriBusinessRatingRequest) {
        ApiResponse<?> response = agriBusinessRatingService.createAgriBusinessRating(agriBusinessRatingRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllAgriBusinessRating() {
        return ResponseEntity.ok(agriBusinessRatingService.getAllAgriBusinessRating());
    }

    @GetMapping("/get/by/agriBusinessRatingByAgriBusinessId/{agriBusinessId}")
    public ResponseEntity<?> getAgriBusinessRatingByAgriBusinessId(@PathVariable("agriBusinessId") Long id) {
        return ResponseEntity.ok(agriBusinessRatingService.getAgriBusinessRatingByAgriBusinessId(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getAgriBusinessRatingsByUserId(@PathVariable Long userId) {
        ApiResponse<?> response = agriBusinessRatingService.getAgriBusinessRatingsByUserId(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{agriBusinessRatingId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteAgriBusinessRating(@PathVariable("agriBusinessRatingId") Long id) {
        return ResponseEntity.ok(agriBusinessRatingService.deleteAgriBusinessRating(id));
    }
}
