package com.EquiFarm.EquiFarm.Staff;

import com.EquiFarm.EquiFarm.Staff.DTO.StaffUserRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff")
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('staff:read')")
    public ResponseEntity<?> getStaffProfile() { return ResponseEntity.ok(staffService.getStaffProfile());}

    @GetMapping("/get/by/staffId/{staffId}")
    public ResponseEntity<?> getStaffById(@PathVariable("staffId") Long id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('staff:update')")
    public ResponseEntity<?> staffProfileUpdate(@RequestBody StaffUserRequest staffUserRequest) {
        return ResponseEntity.ok(staffService.staffProfileUpdate(staffUserRequest));
    }

    @PutMapping("/update/by/userId/{userId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> staffProfileUpdateByUserId( @PathVariable("userId") Long userId,
                                                         @RequestBody StaffUserRequest staffUserRequest) {
        return ResponseEntity.ok(staffService.staffProfileUpdateByUserId(userId, staffUserRequest));
    }
    @DeleteMapping("/delete/{staffId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> staffDelete(@PathVariable("staffId") Long id) {
        return ResponseEntity.ok(staffService.staffDelete(id));
    }

    @PutMapping("/verify/{productId}")
    @PreAuthorize("hasAuthority('staff:update')")
    public ResponseEntity<?> verifyFarmProduct(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(staffService.verifyFarmProduct(productId));
    }

    @PutMapping("/verify/farmer/{farmerId}")
    @PreAuthorize("hasAuthority('staff:update')")
    public ResponseEntity<?> verifyFarmer(@PathVariable("farmerId") Long farmerId) {
        return ResponseEntity.ok(staffService.verifyFarmer(farmerId));
    }

    @PutMapping("/verify/driver/{driverId}")
    @PreAuthorize("hasAuthority('staff:update')")
    public ResponseEntity<?> verifyDriver(@PathVariable("driverId") Long driverId) {
        return ResponseEntity.ok(staffService.verifyDriver(driverId));
    }

    @PutMapping("/verify/agriBusiness/{agriBusinessId}")
    @PreAuthorize("hasAuthority('staff:update')")
    public ResponseEntity<?> verifyAgriBusiness(@PathVariable("agriBusinessId") Long agriBusinessId) {
        return ResponseEntity.ok(staffService.verifyAgriBusiness(agriBusinessId));
    }

    @PutMapping("/verify/manufacturer/{manufacturerId}")
    @PreAuthorize("hasAuthority('staff:update')")
    public ResponseEntity<?> verifyManufacturer(@PathVariable("manufacturerId") Long manufacturerId) {
        return ResponseEntity.ok(staffService.verifyManufacturer(manufacturerId));
    }

    @PutMapping("/verify/serviceProvider/{serviceProviderId}")
    @PreAuthorize("hasAuthority('staff:update')")
    public ResponseEntity<?> verifyServiceProvider(@PathVariable("serviceProviderId") Long serviceProviderId) {
        return ResponseEntity.ok(staffService.verifyServiceProvider(serviceProviderId));
    }

    @PutMapping("/verify/warehouse/{warehouseId}")
    @PreAuthorize("hasAuthority('staff:update')")
    public ResponseEntity<?> verifyWarehouse(@PathVariable("warehouseId") Long warehouseId) {
        return ResponseEntity.ok(staffService.verifyWarehouse(warehouseId));
    }
}