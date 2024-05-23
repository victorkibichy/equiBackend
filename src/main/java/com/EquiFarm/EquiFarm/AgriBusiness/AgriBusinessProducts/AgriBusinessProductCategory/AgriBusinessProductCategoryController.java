package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory.DTO.AgriBusinessProductCategoryRequest;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.DTO.ProductCategoryRequest;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.ProductCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agriBusinessProductCategory")
@Tag(name = "Agribusiness Product Category")
@RequiredArgsConstructor
public class AgriBusinessProductCategoryController {
    private final AgriBusinessProductCategoryService agriBusinessProductCategoryService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> createAgriBusinessProductCategory(@RequestBody AgriBusinessProductCategoryRequest agriBusinessProductCategoryRequest) {
        return ResponseEntity.ok(agriBusinessProductCategoryService.createAgriBusinessProductCategory(agriBusinessProductCategoryRequest));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllAgriBusinessProductCategories() {
        return ResponseEntity.ok(agriBusinessProductCategoryService.getAllAgriBusinessProductCategories());
    }
    @GetMapping("/get/by/agriBusinessProductCategoryId/{productCategoryId}")
    public ResponseEntity<?> getProductCategoryById(@PathVariable("productCategoryId") Long id) {
        return ResponseEntity.ok(agriBusinessProductCategoryService.getAgriBusinessProductCategoryById(id));
    }
    @PutMapping("/update/{agriBusinessProductCategoryId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateAgriBusinessProductCategory(@RequestBody AgriBusinessProductCategoryRequest agriBusinessProductCategoryRequest,
                                                   @PathVariable("agriBusinessProductCategoryId") Long id) {
        return ResponseEntity.ok(agriBusinessProductCategoryService.agriBusinessProductCategoryUpdate(agriBusinessProductCategoryRequest, id));
    }


    @DeleteMapping("/delete/{agriBusinessProductCategoryId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> agriBusinessProductCategoryDelete(@PathVariable("agriBusinessProductCategoryId") Long id) {
        return ResponseEntity.ok(agriBusinessProductCategoryService.agriBusinessProductCategoryDelete(id));
    }

}
