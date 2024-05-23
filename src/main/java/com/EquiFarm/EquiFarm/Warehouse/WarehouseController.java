package com.EquiFarm.EquiFarm.Warehouse;

import com.EquiFarm.EquiFarm.Warehouse.DTO.WarehouseRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouses")
@Tag(name = "Warehouses")
@Slf4j
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;
    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('warehouse:read')")
    public ResponseEntity<?> findWarehouseProfile(){
        return ResponseEntity.ok(warehouseService.fetchWarehouseProfile());
    }
    @GetMapping("/get/by/userId/{userId}")
    public ResponseEntity<?> getWarehouseById(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseByUserId(id));
    }
    @GetMapping("/get/all")
    public ResponseEntity<?> getALlWarehouses(){
        return ResponseEntity.ok(warehouseService.getALlWarehouses());
    }

    @GetMapping("/get/by/value/chain/{valueChainId}")
    public ResponseEntity<?> fetchServiceProvidersByValueChain(@PathVariable("valueChainId") Long valueChainId){
        return ResponseEntity.ok(warehouseService.getAllWarehousesInValueChain(valueChainId));
    }
    @GetMapping("/get/all/vacant")
    public ResponseEntity<?> getVacantWareHouses() {
        return ResponseEntity.ok(warehouseService.getVacantWarehouses());
    }
    @GetMapping("/get/by/capacity/{warehouseCapacity}")
    public ResponseEntity<?> getWarehouseByCapacity(@PathVariable("warehouseCapacity")Double capacity) {
        return ResponseEntity.ok(warehouseService.getWarehouseByCapacity(capacity));
    }
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('warehouse:update')")
    public ResponseEntity<?> warehouseProfileUpdate(
            @RequestBody WarehouseRequest warehouseRequest) {
        return ResponseEntity.ok(warehouseService.warehouseProfileUpdate(warehouseRequest));
    }
    @PutMapping("/update/by/warehouseId/{warehouseId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateByWarehouseId(@RequestBody WarehouseRequest warehouseRequest, @PathVariable("warehouseId") Long warehouseId){
        return ResponseEntity.ok(warehouseService.updateWarehouseByWarehouseId(warehouseRequest, warehouseId));
    }

    @PutMapping("/update/by/userId/{userId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateByUserId(@RequestBody WarehouseRequest warehouseRequest, @PathVariable("userId") Long userId){
        return ResponseEntity.ok(warehouseService.updateWarehouseByUserId(warehouseRequest, userId));
    }

    @DeleteMapping("/delete/{warehouseId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteServiceProvider(
            @PathVariable("warehouseId") Long warehouseId) {
        return ResponseEntity.ok(warehouseService.deleteWarehouse(warehouseId));
    }
}
