package com.EquiFarm.EquiFarm.Driver;

import com.EquiFarm.EquiFarm.Farmer.DTO.FarmerUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EquiFarm.EquiFarm.Driver.DTO.DriverUserRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/drivers")
@Tag(name = "Drivers")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('driver:read')")
    public ResponseEntity<?> getDriverProfile() {
        return ResponseEntity.ok(driverService.getDriverProfile());
    }

    @PutMapping("/profile/update")
    @PreAuthorize("hasAuthority('driver:update')")
    public ResponseEntity<?> updateDriverProfile(@RequestBody DriverUserRequest driverUserRequest) {
        return ResponseEntity.ok(driverService.driverProfileUpdate(driverUserRequest));
    }

    @PutMapping("/update/{driverId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateDriver(@RequestBody DriverUserRequest driverUserRequest, @PathVariable("driverId") Long id) {
        return ResponseEntity.ok(driverService.updateDriver(driverUserRequest, id));
    }

    @GetMapping("/get/by/driverId/{driverId}")
    public ResponseEntity<?> getDriverByDriverId(@PathVariable("driverId") Long id){
        return ResponseEntity.ok(driverService.getDriverByDriverId(id));
    }
    @GetMapping("/get/by/userId/{userId}")
    public ResponseEntity<?> getDriverByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(driverService.getDriverByUserId(userId));
    }
    @GetMapping("/get/by/value/chain/{valueChainId}")
    public ResponseEntity<?> fetchServiceProvidersByValueChain(@PathVariable("valueChainId") Long valueChainId){
        return ResponseEntity.ok(driverService.getAllDriversInValueChain(valueChainId));
    }
    @PutMapping("/update/by/UserId{userId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateDriverProfileByUserId(@RequestBody DriverUserRequest driverUserRequest,
                                                         @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(driverService.updateDriverByUserId(driverUserRequest, userId));
    }


    @GetMapping("/get/all")
    public ResponseEntity<?> getAllDrivers(){
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @DeleteMapping("/delete/{driverId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteDriver(@PathVariable("driverId") Long id){
        return ResponseEntity.ok(driverService.driverDelete(id));

    }


}
