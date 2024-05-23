package com.EquiFarm.EquiFarm.Rating.DriverRating;

import com.EquiFarm.EquiFarm.Rating.DriverRating.DTO.DriverRatingRequest;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/driverRating")
@Tag(name = "Driver Rating")
@RequiredArgsConstructor
public class DriverRatingController {
    private final DriverRatingService driverRatingService;

    @PostMapping("/create")
    public ResponseEntity<?> createDriverRating(@RequestBody DriverRatingRequest driverRatingRequest) {
        return ResponseEntity.ok(driverRatingService.createDriverRating(driverRatingRequest));
    }
    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllDriverRating() {
        return ResponseEntity.ok(driverRatingService.getAllDriverRating());

    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getDriverRatingsByUserId(@PathVariable Long userId) {
        ApiResponse<?> response = driverRatingService.getDriverRatingsByUserId(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get/by/driverRatingByDriverId/{driverId}")
    public ResponseEntity<?> getDriverRatingByDriverId(@PathVariable("driverId") Long id) {
        return ResponseEntity.ok(driverRatingService.getDriverRatingByDriverId(id));
    }
    @DeleteMapping("/delete/{driverRatingId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteDriverRating(
            @PathVariable("driverRatingId") Long id) {
        return ResponseEntity.ok(driverRatingService.deleteDriverRating(id));
    }

}
