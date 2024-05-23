package com.EquiFarm.EquiFarm.FarmTech;

import com.EquiFarm.EquiFarm.FarmTech.DTO.FarmTechRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/farmTech")
@Tag(name = "Farm-Tech Owner")
@RequiredArgsConstructor
public class FarmTechController {
    private final FarmTechService farmTechService;
    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('farmTechOwner:read')")
    public ResponseEntity<?> getFarmTechOwner() {
        return ResponseEntity.ok(farmTechService.getFarmTechOwnerProfile());
    }

    @GetMapping("/get/by/farmTechId/{farmTechId}")
    public ResponseEntity<?> getfarmTechById(@PathVariable("farmTechId") Long id) {
        return ResponseEntity.ok(farmTechService.getFarmTechOwnerById(id));
    }

    @GetMapping("/get/by/value/chain/{valueChainId}")
    public ResponseEntity<?> fetchFarmTechByValueChain(@PathVariable("valueChainId") Long valueChainId) {
        return ResponseEntity.ok(farmTechService.getAllFarmTechInValueChain(valueChainId));
    }

    @GetMapping("/get/by/userId/{userId}")
    public ResponseEntity<?> getFarmTechByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(farmTechService.getFarmTechByUserId(userId));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllFarmTechs() {
        return ResponseEntity.ok(farmTechService.getAllFarmTechs());
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('farmTechOwner:update')")
    public ResponseEntity<?> farmTechProfileUpdate(@RequestBody FarmTechRequest farmTechRequest) {
        return ResponseEntity.ok(farmTechService.farmTechProfileUpdate(farmTechRequest));
    }

    @PutMapping("/update/by/farmTechId/{farmTechId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateByfarmTechId(@RequestBody FarmTechRequest farmTechRequest, @PathVariable("farmTechId") Long farmTechId){
        return ResponseEntity.ok(farmTechService.updateFarmTechByFarmTechId(farmTechRequest, farmTechId));
    }

    @DeleteMapping("/delete/{farmTechId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> farmTechDelete(@PathVariable("farmTechId") Long id) {
        return ResponseEntity.ok(farmTechService.farmTechDelete(id));
    }



}
