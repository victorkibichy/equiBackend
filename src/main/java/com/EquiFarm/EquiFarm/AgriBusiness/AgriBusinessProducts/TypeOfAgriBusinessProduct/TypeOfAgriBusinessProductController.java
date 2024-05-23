package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.TypeOfAgriBusinessProduct;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.TypeOfAgriBusinessProduct.DTO.TypeOfAgriBusinessProductRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/typeOfAgribusinessProducts")
@Tag(name = "Type of AgriBusiness Products")
@RequiredArgsConstructor
public class TypeOfAgriBusinessProductController {
    private final TypeOfAgriBusinessProductService typeOfAgriBusinessProductService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> addTypeOfAgriBusinessProduct(@RequestBody TypeOfAgriBusinessProductRequest typeOfAgriBusinessProductRequest) {
        return ResponseEntity.ok(typeOfAgriBusinessProductService.addTypeOfAgriBusinessProduct(typeOfAgriBusinessProductRequest));
    }
    @PutMapping("/update/{typeOfAgriBusinessProductId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateTypeOfAgriBusinessProduct(@RequestBody TypeOfAgriBusinessProductRequest typeOfAgriBusinessProductRequest,
                                                 @PathVariable("typeOfProductId") Long id) {
        return ResponseEntity.ok(typeOfAgriBusinessProductService.typeOfAgriBusinessProductUpdate(typeOfAgriBusinessProductRequest, id));
    }

    @PatchMapping("/update/{typeOfProductId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateTypeOfAgriBusinessProductPartially(@RequestBody TypeOfAgriBusinessProductRequest typeOfAgriBusinessProductRequest,
                                                          @PathVariable("typeOfAgriBusinessProductId") Long id) {
        return ResponseEntity.ok(typeOfAgriBusinessProductService.typeOfAgriBusinessProductUpdate(typeOfAgriBusinessProductRequest, id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllTypesOfProducts() {
        return ResponseEntity.ok(typeOfAgriBusinessProductService.getAllTypeOfAgriBusinessProducts());
    }
    @GetMapping("/get/by/typeOfAgriBusinessProductId/{typeOfAgriBusinessProductId}")
    public ResponseEntity<?> getTypeOfAgriBusinessProductById(@PathVariable("typeOfAgriBusinessProductId") Long id) {
        return ResponseEntity.ok(typeOfAgriBusinessProductService.getTypeOfAgriBusinessProductById(id));
    }

    @DeleteMapping("/delete/{typeOfAgriBusinessProductId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteTypeOfAgriBusinessProduct(@PathVariable("typeOfAgriBusinessProductId") Long id) {
        return ResponseEntity.ok(typeOfAgriBusinessProductService.typeOfAgriBusinessProductDelete(id));
    }


}
