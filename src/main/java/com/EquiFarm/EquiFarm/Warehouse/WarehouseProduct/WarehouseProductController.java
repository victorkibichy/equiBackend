package com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct;

import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.DTO.QualityCheckRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouse/products")
@Tag(name = "Warehouse Products")
@Slf4j
@RequiredArgsConstructor
public class WarehouseProductController {
    private final WarehouseProductService warehouseProductService;

    @GetMapping("get/by/id{whProductId}")
    @PreAuthorize("hasAuthority('warehouse:read')")
    public ResponseEntity<?>getById(@PathVariable("whProductId") Long whProductId){
        return ResponseEntity.ok(warehouseProductService.getProductById(whProductId));
    }



    @GetMapping("/owner/filter")
    @PreAuthorize("hasAuthority('warehouse:read')")
    public ResponseEntity<?> filterProducts(@RequestParam(required = false) LocalDate expiryDate,
                                            @RequestParam(required = false) Boolean qualityCheckDone,
                                            @RequestParam(required = false) Boolean verified,
                                            @RequestParam(required = false) Boolean perishable,
                                            @RequestParam(required = false) Long farmerId,
                                            @RequestParam(required = false) Long productCategory,
                                            @RequestParam(required = false) Long typeOfProductId){
        return ResponseEntity.ok(warehouseProductService.getAllProductsForWarehouse(expiryDate, qualityCheckDone,verified, perishable, farmerId, productCategory, typeOfProductId));
    }

    @GetMapping("/market/filter")
    public ResponseEntity<?> filterProducts(@RequestParam(required = false) Long warehouseId,
                                            @RequestParam(required = false) Boolean isPerishable,
                                            @RequestParam(required = false) Long productCategory,
                                            @RequestParam(required = false) Long typeOfProductId) {
        return ResponseEntity.ok(warehouseProductService.getAllProductsForMarketPlace(warehouseId, productCategory, typeOfProductId, isPerishable));
    }

    @GetMapping("/get/by/status/{status}")
    @PreAuthorize("hasAuthority('warehouse:read')")
    public ResponseEntity<?> filterByStatus(@PathVariable("status") WHProductStatus status){
        return ResponseEntity.ok(warehouseProductService.findByStatus(status));
    }
    @PutMapping("/verify/{productId}")
    @PreAuthorize("hasAuthority('warehouse:update')")
    public ResponseEntity<?> verifyProduct(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(warehouseProductService.verifyWHProduct(productId));
    }

    @PostMapping("/perform/test/whProductId/{whProductId}")
    @PreAuthorize("hasAuthority('warehouse:update')")
    public ResponseEntity<?> performTest(@PathVariable("whProductId") Long whProductId,
                                         @RequestBody List<QualityCheckRequest> checkRequest){
        return ResponseEntity.ok(warehouseProductService.performQualityChecks(whProductId, checkRequest));
    }
//    @GetMapping("/get/quality/checks")
//    public ResponseEntity<?> allQualityChecks(Long whProduct){
//        return ResponseEntity.ok(warehouseProductService.findAllWHProductQualityChecks(whProduct));
//    }
    @PutMapping("/sell/by/productId/{whProductId}")
    @PreAuthorize("hasAuthority('farmer:update')")
    public ResponseEntity<?> sellProduct(@PathVariable("whProductId") Long whProductId) {
        return ResponseEntity.ok(warehouseProductService.sellProductFromWarehouse(whProductId));
    }

    @DeleteMapping("/delete/{whProductId}")
    @PreAuthorize("hasAuthority('warehouse:delete')")
    public ResponseEntity<?> deleteWarehouseProduct(@PathVariable("whProductId") Long whProductId){
        return ResponseEntity.ok(warehouseProductService.deleteWHProduct(whProductId));
    }

}
