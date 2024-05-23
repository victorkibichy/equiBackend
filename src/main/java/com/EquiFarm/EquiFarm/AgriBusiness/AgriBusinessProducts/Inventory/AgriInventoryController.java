package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.Inventory;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.UnitsRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agribusiness/product/inventory")
@Tag(name = "Agri-Business Products Inventory")
@RequiredArgsConstructor
public class AgriInventoryController {
    private final AgriInventoryService agriInventoryService;
    @GetMapping("/get/by/agribusiness/product/id/{productId}")
    @PreAuthorize("hasAuthority('agribusiness:read')")
    public ResponseEntity<?> getByProductId(@PathVariable("productId") Long productId){
        return ResponseEntity.ok(agriInventoryService.getAgriInventory(productId));
    }
    @PostMapping("/add/available/units")
    @PreAuthorize("hasAuthority('agribusiness:create')")
    public ResponseEntity<?> addAvailableUnits(@RequestBody UnitsRequest unitsRequest) {
        return ResponseEntity.ok(agriInventoryService.updateAvailableUnits(unitsRequest));
    }
}
