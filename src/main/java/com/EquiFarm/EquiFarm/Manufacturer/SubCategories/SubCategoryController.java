package com.EquiFarm.EquiFarm.Manufacturer.SubCategories;

import com.EquiFarm.EquiFarm.Manufacturer.Categories.Category;
import com.EquiFarm.EquiFarm.Manufacturer.SubCategories.DTO.SubCategoryRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manufacturing/sub/category")
@Tag(name = "Manufacturing Sub Category")
@RequiredArgsConstructor
public class SubCategoryController {
    private final SubCategoryService subCategoryService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> createSubCategory(@RequestBody SubCategoryRequest subCategoryRequest) {
        return ResponseEntity.ok(subCategoryService.addSubCategory(subCategoryRequest));
    }
    @GetMapping("/get/all")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(subCategoryService.findAllSubCategories());
    }

    @GetMapping("/get/by/id/{subCategoryId}")
    public ResponseEntity<?> getSubCategoryById(@PathVariable("subCategoryId") Long subCategoryId){
        return ResponseEntity.ok(subCategoryService.findSubCategoryById(subCategoryId));
    }

    @GetMapping("get/by/category/id/{categoryId}")
    public ResponseEntity<?> getSubCategoriesByCategory(@PathVariable("categoryId") Long categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        return ResponseEntity.ok(subCategoryService.findByCategory(category));
    }


    @PutMapping("/update/{subCategoryId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateSubCategory(@RequestBody SubCategoryRequest subCategoryRequest, @PathVariable("subCategoryId") Long subCategoryId){
        return ResponseEntity.ok(subCategoryService.updateSubCategory(subCategoryRequest, subCategoryId));
    }
    @DeleteMapping("/delete/{subCategoryId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteSubCategory(@PathVariable("subCategoryId") Long subCategoryId){
        return ResponseEntity.ok(subCategoryService.deleteSubCategory(subCategoryId));
    }
}
