package com.EquiFarm.EquiFarm.AgriBusiness;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.EquiFarm.EquiFarm.AgriBusiness.DTO.AgriBusinessUserRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/agriBusiness")
@Tag(name = "Agri-Business Owner")
@RequiredArgsConstructor
public class AgriBusinessController {
    private final AgriBusinessService agriBusinessService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('agriBusinessOwner:read')")
    public ResponseEntity<?> getAgriBusinessOwner() {
        return ResponseEntity.ok(agriBusinessService.getAgriBusinessOwnerProfile());
    }

    @GetMapping("/get/by/agriBusinessId/{agriBusinessId}")
    public ResponseEntity<?> getAgriBusinessById(@PathVariable("agriBusinessId") Long id) {
        return ResponseEntity.ok(agriBusinessService.getAgriBusinessOwnerById(id));
    }

    @GetMapping("/get/by/value/chain/{valueChainId}")
    public ResponseEntity<?> fetchAgriBusinessOwnerByValueChain(@PathVariable("valueChainId") Long valueChainId) {
        return ResponseEntity.ok(agriBusinessService.getAllAgribusinessInValueChain(valueChainId));
    }

    @GetMapping("/get/by/userId/{userId}")
    public ResponseEntity<?> getAgriBusinessByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(agriBusinessService.getAgriBusinessOwnerByUserId(userId));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllAgriBusinesses() {
        return ResponseEntity.ok(agriBusinessService.getAllAgribusinesses());
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('agriBusinessOwner:update')")
    public ResponseEntity<?> agriBusinessProfileUpdate(@RequestBody AgriBusinessUserRequest agriBusinessUserRequest) {
        return ResponseEntity.ok(agriBusinessService.agriBusinessUpdate(agriBusinessUserRequest));
    }

    @PutMapping("/update/{agriBusinessId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateAgriBusiness(@PathVariable("agriBusinessId") Long id,
            @RequestBody AgriBusinessUserRequest agriBusinessUserRequest) {
        return ResponseEntity.ok(agriBusinessService.updateAgriBusiness(id, agriBusinessUserRequest));
    }

    @PutMapping("/update/by/userId/{userId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateAgriBusinessByUserId(@PathVariable("userId") Long userId,
                                                @RequestBody AgriBusinessUserRequest agriBusinessUserRequest) {
        return ResponseEntity.ok(agriBusinessService.updateAgriBusinessByUserId(userId, agriBusinessUserRequest));
    }

    @DeleteMapping("/delete/{agriBusinessId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> agriBusinessDelete(@PathVariable("agriBusinessId") Long id) {
        return ResponseEntity.ok(agriBusinessService.agriBusinessDelete(id));
    }

}
