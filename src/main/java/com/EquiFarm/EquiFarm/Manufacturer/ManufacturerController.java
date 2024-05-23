package com.EquiFarm.EquiFarm.Manufacturer;

import com.EquiFarm.EquiFarm.Manufacturer.Categories.Category;
import com.EquiFarm.EquiFarm.Manufacturer.SubCategories.SubCategory;
import com.EquiFarm.EquiFarm.Manufacturer.DTO.ManufacturerRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manufacturers")
@Tag(name = "Manufacturers")
@Slf4j
@RequiredArgsConstructor
public class ManufacturerController {
    private final ManufacturerService manufacturerService;
    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('manufacturer:read')")
    public ResponseEntity<?> findManufacturerProfile(){
        return ResponseEntity.ok(manufacturerService.fetchManufacturerProfile());
    }
    @GetMapping("/get/by/userId/{userId}")
    public ResponseEntity<?> getManufacturerById(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(manufacturerService.getManufacturerByUserId(id));
    }
    @GetMapping("/get/all")
    public ResponseEntity<?> getALlManufacturers(){
        return ResponseEntity.ok(manufacturerService.getALlManufacturers());
    }
    @GetMapping("/get/by/value/chain/{valueChainId}")
    public ResponseEntity<?> fetchServiceProvidersByValueChain(@PathVariable("valueChainId") Long valueChainId){
        return ResponseEntity.ok(manufacturerService.getAllManufacturersInValueChain(valueChainId));
    }
    @GetMapping("/get/by/category/id{categoryId}")
    public ResponseEntity<?> getALlByCategory(@PathVariable("categoryId") Long categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        return ResponseEntity.ok(manufacturerService.getByCategory(category));
    }

    @GetMapping("/get/by/subcategory/id{subCategoryId}")
    public ResponseEntity<?> getALlBySubCategory(@PathVariable("subCategoryId") Long subCategoryId){
        SubCategory subCategory = new SubCategory();
        subCategory.setId(subCategoryId);
        return ResponseEntity.ok(manufacturerService.getBySubCategory(subCategory));
    }
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('manufacturer:update')")
    public ResponseEntity<?> manufacturerProfileUpdate(
            @RequestBody ManufacturerRequest manufacturerRequest) {
        return ResponseEntity.ok(manufacturerService.manufacturerProfileUpdate(manufacturerRequest));
    }
    @PutMapping("/update/by/manufacturerId/{manufacturerId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateByManufacturerId(@RequestBody ManufacturerRequest manufacturerRequest, @PathVariable("manufacturerId") Long manufacturerId){
        return ResponseEntity.ok(manufacturerService.updateManufacturerByManufacturerId(manufacturerRequest, manufacturerId));
    }

    @PutMapping("/update/by/userId/{userId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateByUserId(@RequestBody ManufacturerRequest manufacturerRequest, @PathVariable("userId") Long userId){
        return ResponseEntity.ok(manufacturerService.updateManufacturerByUserId(manufacturerRequest, userId));
    }

    @DeleteMapping("/delete/{manufacturerId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteServiceProvider(
            @PathVariable("manufacturerId") Long manufacturerId) {
        return ResponseEntity.ok(manufacturerService.deleteManufacturer(manufacturerId));
    }
}
