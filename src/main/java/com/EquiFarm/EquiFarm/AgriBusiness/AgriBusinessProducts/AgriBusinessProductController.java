package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.DTO.AgriBusinessProductDispImageRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.DTO.AgriBusinessProductRequest;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.SellingPoint;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agriBusinessProduct")
@Tag(name = "AgriBusiness Product")
@RequiredArgsConstructor
public class AgriBusinessProductController {
    private final AgriBusinessProductService agriBusinessProductService;

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllAgriBusinessProducts(@RequestParam(required = false) Long categoryId,
                                                @RequestParam(required = false) Long productTypeId,
                                                @RequestParam(required = false) Long agriBusinessId,
                                                @RequestParam(required = false) Boolean verified,
                                                @RequestParam(required = false) Boolean onStock,
                                                @RequestParam(required = false) SellingPoint sellingPoint) {
        return ResponseEntity.ok(agriBusinessProductService.getAllAgriBusinessProducts(categoryId, productTypeId, agriBusinessId,verified, onStock, sellingPoint));
    }
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('agriBusinessOwner:create')")
    public ResponseEntity<?> addAgriBusinessProduct(@RequestBody AgriBusinessProductRequest agriBusinessProductRequest) {
        return ResponseEntity.ok(agriBusinessProductService.createAgriBusinessProduct(agriBusinessProductRequest));
    }
    @GetMapping("/get/by/productId/{productId}")
    public ResponseEntity<?> getAgriBusinessProductById(@PathVariable("productId") Long id) {
        return ResponseEntity.ok(agriBusinessProductService.getAgriBusinessProductById(id));
    }

    @GetMapping("/get/by/agriBusinessId/{agriBusinessId}")
    public ResponseEntity<?> getAgriBusinessProductsByFarmerId(@PathVariable("agriBusinessId") Long id) {
        return ResponseEntity.ok(agriBusinessProductService.getAgriBusinessProductByAgriBusinessId(id));
    }
    @PutMapping("/update/{productId}")
    @PreAuthorize("hasAuthority('agriBusinessOwner:update')")
    public ResponseEntity<?> updateAgriBusinessProduct(@PathVariable("productId") Long id,
                                               @RequestBody AgriBusinessProductRequest agriBusinessProductRequest) {
        return ResponseEntity.ok(agriBusinessProductService.updateAgriBusinessProducts(agriBusinessProductRequest, id));
    }

    @PatchMapping("/update/{productId}")
    @PreAuthorize("hasAuthority('agriBusinessOwner:update')")
    public ResponseEntity<?> updateAgriBusinessProductPartial(@PathVariable("productId") Long id,
                                                      @RequestBody AgriBusinessProductRequest agriBusinessProductRequest) {
        return ResponseEntity.ok(agriBusinessProductService.updateAgriBusinessProducts(agriBusinessProductRequest, id));
    }

    @PutMapping("/remove/displayImage/{productId}")
    @PreAuthorize("hasAuthority('agriBusinessOwner:update')")
    public ResponseEntity<?> removeAgriBusinessDisplayImages(@PathVariable("productId") Long id,
                                                 @RequestBody AgriBusinessProductDispImageRequest agriBusinessProductDispImageRequest) {
        return ResponseEntity.ok(agriBusinessProductService.removeAgriBusinessDisplayImages(agriBusinessProductDispImageRequest, id));
    }


    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteAgriBusinessProduct(@PathVariable("productId") Long id) {
        return ResponseEntity.ok(agriBusinessProductService.agriBusinessProductDelete(id));

    }

}
