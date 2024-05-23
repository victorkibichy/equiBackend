package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Inventory;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.UnitsRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/farmProducts/inventory")
@Tag(name = "Farm Products Inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    @GetMapping("/get/by/farm/product/id/{productId}")
    @PreAuthorize("hasAuthority('farmer:read')")
    public ResponseEntity<?> getByProductId(@PathVariable("productId") Long productId){
        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }
    @PostMapping("/add/available/units")
    @PreAuthorize("hasAuthority('farmer:create')")
    public ResponseEntity<?> addAvailableUnits(@RequestBody UnitsRequest unitsRequest) {
        return ResponseEntity.ok(inventoryService.updateAvailableUnits(unitsRequest));
    }
}
