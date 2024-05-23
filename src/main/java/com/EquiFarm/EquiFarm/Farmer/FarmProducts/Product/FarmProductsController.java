package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product;


import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.UnitsRequest;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.DTO.WHProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.FarmProductDispImageRequest;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.FarmProductsRequest;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.DTO.WHProductRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/farmProducts")
@Tag(name = "Farm Products")
@RequiredArgsConstructor
public class FarmProductsController {
    private final FarmProductService farmProductService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('farmer:create')")
    public ResponseEntity<?> addFarmProduct(@RequestBody FarmProductsRequest farmProductsRequest) {
        return ResponseEntity.ok(farmProductService.createFarmProduct(farmProductsRequest));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllFarmProducts(@RequestParam(required = false) Long categoryId,
                                                @RequestParam(required = false) Long productTypeId,


                                                @RequestParam(required = false) Long farmerId,
                                                @RequestParam(required = false) Boolean verified,
                                                @RequestParam(required = false) Boolean onStock,
                                                @RequestParam(required = false) Boolean preListed
//                                                @RequestParam(required = false) SellingPoint sellingPoint
    ) {
        return ResponseEntity.ok(farmProductService.getAllFarmProducts(categoryId, productTypeId, farmerId,verified, onStock, preListed));

    }

    @GetMapping("/get/paginated/data")
    public Page<FarmProducts> getPaginatedData(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return farmProductService.getPaginatedProducts(page, size);
    }

    @PutMapping("/upload/by/warehouseId/{warehouseId}/farmProductId/{farmProductId}")
    @PreAuthorize("hasAuthority('farmer:create')")
    public ResponseEntity<?> uploadProduct(@PathVariable("warehouseId") Long warehouseId,
                                           @PathVariable("farmProductId") Long farmProductId,
                                           @RequestBody WHProductRequest whProductRequest) {
        return ResponseEntity.ok(farmProductService.uploadProductToWarehouse(warehouseId, farmProductId, whProductRequest));
    }

    @GetMapping("/get/by/productId/{productId}")
    public ResponseEntity<?> getFarmProductById(@PathVariable("productId") Long id) {
        return ResponseEntity.ok(farmProductService.getFarmProductsById(id));
    }

    @GetMapping("/get/by/farmersId/{farmerId}")
    public ResponseEntity<?> getFarmProductsByFarmerId(@PathVariable("farmerId") Long id) {
        return ResponseEntity.ok(farmProductService.getFarmProductByFarmerId(id));
    }

    @PutMapping("/update/{productId}")
    @PreAuthorize("hasAuthority('farmer:update')")
    public ResponseEntity<?> updateFarmProduct(@PathVariable("productId") Long id,
                                               @RequestBody FarmProductsRequest farmProductsRequest) {
        return ResponseEntity.ok(farmProductService.updateFarmProduct(farmProductsRequest, id));
    }


    @PatchMapping("/update/{productId}")
    @PreAuthorize("hasAuthority('farmer:update')")
    public ResponseEntity<?> updateFarmProductPartial(@PathVariable("productId") Long id,
                                                      @RequestBody FarmProductsRequest farmProductsRequest) {
        return ResponseEntity.ok(farmProductService.updateFarmProduct(farmProductsRequest, id));
    }

    @PutMapping("/remove/displayImage/{productId}")
    @PreAuthorize("hasAuthority('farmer:update')")
    public ResponseEntity<?> removeDisplayImages(@PathVariable("productId") Long id,
                                                 @RequestBody FarmProductDispImageRequest farmProductDispImageRequest) {
        return ResponseEntity.ok(farmProductService.removeDisplayImages(farmProductDispImageRequest, id));
    }

    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteFarmProduct(@PathVariable("productId") Long id) {
        return ResponseEntity.ok(farmProductService.farmProductDelete(id));

    }

    @PutMapping("/applyDiscount/{farmProductId}")
    @PreAuthorize("hasAuthority('farmer')")
    public ResponseEntity<?> applyDiscount(@PathVariable("farmerId") Long farmProductId)
//                                           @RequestParam("discount") Double discount)
    {
        return ResponseEntity.ok(farmProductService.applyDiscount(farmProductId));


    }
}
