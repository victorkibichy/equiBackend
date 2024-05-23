package com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts.DTO.TypeOfProductRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

@RestController
@RequestMapping("/api/v1/typeOfProducts")
@Tag(name = "Type of Products")
@RequiredArgsConstructor
public class TypeOfProductController {
    private final TypeOfProductsService typeOfProductsService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> addTypeOfProduct(@RequestBody TypeOfProductRequest typeOfProductRequest) {
        return ResponseEntity.ok(typeOfProductsService.addTypeOfProduct(typeOfProductRequest));
    }

    @PutMapping("/update/{typeOfProductId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateTypeOfProduct(@RequestBody TypeOfProductRequest typeOfProductRequest,
            @PathVariable("typeOfProductId") Long id) {
        return ResponseEntity.ok(typeOfProductsService.typeOfProductUpdate(typeOfProductRequest, id));
    }

    @PatchMapping("/update/{typeOfProductId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateTypeOfProductPartially(@RequestBody TypeOfProductRequest typeOfProductRequest,
            @PathVariable("typeOfProductId") Long id) {
        return ResponseEntity.ok(typeOfProductsService.typeOfProductUpdate(typeOfProductRequest, id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllTypesOfProducts(@RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(typeOfProductsService.getAllTypeOfProducts(categoryId));
    }

    @GetMapping("/get/by/typeOfProductId/{typeOfProductId}")
    public ResponseEntity<?> getTypeOfProductById(@PathVariable("typeOfProductId") Long id) {
        return ResponseEntity.ok(typeOfProductsService.getTypeOfProductById(id));
    }

    @DeleteMapping("/delete/{typeOfProductId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteTypeOfProduct(@PathVariable("typeOfProductId") Long id) {
        return ResponseEntity.ok(typeOfProductsService.typeOfProductDelete(id));
    }

}
