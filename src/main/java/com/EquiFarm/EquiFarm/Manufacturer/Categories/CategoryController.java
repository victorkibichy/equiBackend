package com.EquiFarm.EquiFarm.Manufacturer.Categories;

import com.EquiFarm.EquiFarm.Manufacturer.Categories.DTO.CategoryRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manufacturing/category")
@Tag(name = "Manufacturing Category")
@RequiredArgsConstructor
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> createTypeOfService(@RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.addCategory(categoryRequest));
    }
    @GetMapping("/get/all")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @GetMapping("/get/by/id/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable("categoryId") Long categoryId){
        return ResponseEntity.ok(categoryService.findCategoryById(categoryId));
    }

    @PutMapping("/update/{categoryId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateCategory(@RequestBody CategoryRequest categoryRequest, @PathVariable("categoryId") Long categoryId){
        return ResponseEntity.ok(categoryService.updateCategory(categoryRequest, categoryId));
    }
    @DeleteMapping("/delete/{categoryId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteCategory(@PathVariable("categoryId") Long categoryId){
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }
}
