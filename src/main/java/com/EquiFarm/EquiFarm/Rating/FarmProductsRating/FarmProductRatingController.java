package com.EquiFarm.EquiFarm.Rating.FarmProductsRating;

import com.EquiFarm.EquiFarm.Rating.FarmProductsRating.DTO.FarmProductRatingRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/farmProductRating")
@Tag(name = "Farm Product Rating")
@RequiredArgsConstructor
public class FarmProductRatingController {
    private final FarmProductRatingService farmProductRatingService;

    @PostMapping("/create")
    public ResponseEntity<?> createFarmProductRating(@RequestBody FarmProductRatingRequest
                                                                 farmProductRatingRequest) {
        return ResponseEntity.ok(farmProductRatingService.createFarmProductRating(farmProductRatingRequest));
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllFarmProductsRating() {
        return ResponseEntity.ok(farmProductRatingService.getAllFarmProductsRating());
    }

    @GetMapping("/get/by/farmProductRatingByFarmProductId/{farmProductId}")
    public ResponseEntity<?> getFarmProductRatingByFarmProductId(@PathVariable("farmProductId") Long id) {
        return ResponseEntity.ok(farmProductRatingService.getFarmProductRatingByFarmProductId(id));
    }

    @GetMapping("/get/by/farmProductRatingByUserId/{userId}")
    public ResponseEntity<?> getFarmProductRatingByUserId(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(farmProductRatingService.getFarmProductRatingByUserId(id));
    }

    @DeleteMapping("/delete/{farmProductRatingId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteFarmProductRating(@PathVariable("farmProductRatingId") Long id) {
        return ResponseEntity.ok(farmProductRatingService.deleteFarmProductRating(id));
    }

    @GetMapping("/get/Average/farmProductRatingByFarmProductId/{farmProductId}")
    public ResponseEntity<?> getAverageFarmProductRatingByFarmProductId(
            @PathVariable("farmProductId") Long id) {
        return ResponseEntity.ok(farmProductRatingService.getAverageFarmProductRatingByFarmProductId(id));
    }
}
