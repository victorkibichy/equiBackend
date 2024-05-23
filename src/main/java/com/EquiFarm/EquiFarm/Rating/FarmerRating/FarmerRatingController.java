package com.EquiFarm.EquiFarm.Rating.FarmerRating;

import com.EquiFarm.EquiFarm.Rating.FarmerRating.DTO.FarmerRatingRequest;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/farmerRating")
@Tag(name = "Farmer Rating")
@RequiredArgsConstructor
public class FarmerRatingController {
    private final FarmerRatingService farmerRatingService;

    @PostMapping("/create")
    public ResponseEntity<?> createFarmerRating(@RequestBody FarmerRatingRequest farmerRatingRequest) {
        ApiResponse<?> response = farmerRatingService.createFarmerRating(farmerRatingRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllFarmerRating() {
        return ResponseEntity.ok(farmerRatingService.getAllFarmerRating());

    }

    @GetMapping("/get/by/farmerRatingByFarmerId/{farmerId}")
    public ResponseEntity<?> getFarmerRatingByFarmerId(@PathVariable("farmerId") Long id) {
        return ResponseEntity.ok(farmerRatingService.getFarmerRatingByFarmerId(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getFarmerRatingsByUserId(@PathVariable Long userId) {
        ApiResponse<?> response = farmerRatingService.getFarmerRatingsByUserId(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{farmerRatingId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteFarmerRating(
            @PathVariable("farmerId") Long id) {
        return ResponseEntity.ok(farmerRatingService.deleteFarmerRating(id));
    }

    @GetMapping("/get/Average/FarmerRatingByFarmerId/{farmerId}")
    public ResponseEntity<?> getAverageFarmerRatingByFarmerId(
            @PathVariable("farmerId") Long id) {
        return ResponseEntity.ok(farmerRatingService.getAverageFarmerRatingByFarmerId(id));

    }
}
