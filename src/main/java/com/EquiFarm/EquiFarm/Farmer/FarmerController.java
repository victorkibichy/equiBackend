package com.EquiFarm.EquiFarm.Farmer;

import com.EquiFarm.EquiFarm.Farmer.DTO.FarmerUserRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/farmers")
@Tag(name = "Farmers")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('')")
public class FarmerController {

    private final FarmerService farmerService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('farmer:read')")
    public ResponseEntity<?> getFarmersProfile() {
        return ResponseEntity.ok(farmerService.getFarmerProfile());
    }

    @GetMapping("/get/by/farmerId/{farmerId}")
    public ResponseEntity<?> getFarmerByFarmerId(@PathVariable("farmerId") Long id) {
        return ResponseEntity.ok(farmerService.getFarmerByFarmerId(id));
    }

    @GetMapping("/get/by/userId/{userId}")
    public ResponseEntity<?> getFarmerByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(farmerService.getFarmerByUserId(userId));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllFarmers() {
        return ResponseEntity.ok(farmerService.getAllFarmers());
    }

    @GetMapping("/get/by/value/chain/{valueChainId}")
    public ResponseEntity<?> fetchFarmersByValueChain(@PathVariable("valueChainId") Long valueChainId) {
        return ResponseEntity.ok(farmerService.getAllFarmersInValueChain(valueChainId));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('farmer:update')")
    public ResponseEntity<?> farmerProfileUpdate(@RequestBody FarmerUserRequest farmerUserRequest) {

        return ResponseEntity.ok(farmerService.farmerProfileUpdate(farmerUserRequest));
    }

    @PutMapping("/update/by/farmerId{farmerId}")
    @PreAuthorize("hasAuthority('admin:update') || hasAuthority('staff:update')")
    public ResponseEntity<?> updateFarmerProfiler(@RequestBody FarmerUserRequest farmerUserRequest,
            @PathVariable("farmerId") Long id) {
        return ResponseEntity.ok(farmerService.updateFarmerProfile(farmerUserRequest, id));
    }
    @PutMapping("/update/by/UserId{userId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateFarmerProfileByUserId(@RequestBody FarmerUserRequest farmerUserRequest,
                                                  @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(farmerService.updateFarmerByUserId(farmerUserRequest, userId));
    }

    // @PutMapping("/remove/type/of/farming")
    // @PreAuthorize("hasAuthority('farmer:update')")
    // public ResponseEntity<?> removeFarmerTypeOfFarming(
    //         @RequestBody FarmerTypeOfFarmingRequest farmerTypeOfFarmingRequest) {
    //     return ResponseEntity.ok(farmerService.removeTypeofFarming(farmerTypeOfFarmingRequest));
    // }

    @DeleteMapping("/delete/{farmerId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteFarmerTypeOfFarming(@PathVariable("farmerId") Long id) {
        return ResponseEntity.ok(farmerService.farmerDelete(id));
    }

}
