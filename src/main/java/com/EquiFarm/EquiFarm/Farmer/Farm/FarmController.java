package com.EquiFarm.EquiFarm.Farmer.Farm;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EquiFarm.EquiFarm.Farmer.Farm.DTO.FarmRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/farms")
@Tag(name = "Farms")
@RequiredArgsConstructor
public class FarmController {
    private final FarmService farmService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> addFarm(@RequestBody FarmRequest farmRequest) {
        return ResponseEntity.ok(farmService.addFarm(farmRequest));
    }

    @GetMapping("/get/by/farmId/{farmId}")
    public ResponseEntity<?> getFarmByFarmId(@PathVariable("farmId") Long id) {
        return ResponseEntity.ok(farmService.getFarmByFarmId(id));
    }

    @PutMapping("/update/{farmId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateFarm(@PathVariable("farmId") Long id, @RequestBody FarmRequest farmRequest) {
        return ResponseEntity.ok(farmService.updateFarm(farmRequest, id));
    }

    @PatchMapping("/update/{farmId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateFarmPartially(@PathVariable("farmId") Long id,
            @RequestBody FarmRequest farmRequest) {
        return ResponseEntity.ok(farmService.updateFarm(farmRequest, id));
    }

    @GetMapping("/get/by/farmerId/{farmerId}")
    public ResponseEntity<?> getFarmByFarmerId(@PathVariable("farmerId") Long id) {
        return ResponseEntity.ok(farmService.getFarmByFarmerId(id));
    }

    @PutMapping("/remove/farmer/{farmId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> removeFarmOwner(@PathVariable("farmId") Long id, @RequestBody FarmRequest farmRequest) {
        return ResponseEntity.ok(farmService.removeFarmerFromFarm(farmRequest, id));
    }

    @DeleteMapping("/delete/{farmId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteFarm(@PathVariable("farmId") Long id){
        return ResponseEntity.ok(farmService.farmDelete(id));
    }


}
