package com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.DTO.ProductCategoryRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/productCategories")
@Tag(name = "Product Category")
@RequiredArgsConstructor
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> createProductCategory(@RequestBody ProductCategoryRequest productCategoryRequest) {
        return ResponseEntity.ok(productCategoryService.createProductCategory(productCategoryRequest));
    }

    @PutMapping("/update/{productCategoryId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateProductCategory(@RequestBody ProductCategoryRequest productCategoryRequest,
            @PathVariable("productCategoryId") Long id) {
        return ResponseEntity.ok(productCategoryService.productCategoryUpdate(productCategoryRequest, id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllProductCategories() {
        return ResponseEntity.ok(productCategoryService.getAllProductCategories());
    }

    @GetMapping("/get/by/productCategoryId/{productCategoryId}")
    public ResponseEntity<?> getProductCategoryById(@PathVariable("productCategoryId") Long id) {
        return ResponseEntity.ok(productCategoryService.getTypeOfCategoryById(id));
    }

    @DeleteMapping("/delete/{productCategoryId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> productCategoryDelete(@PathVariable("productCategoryId") Long id) {
        return ResponseEntity.ok(productCategoryService.productCategoryDelete(id));
    }

}
