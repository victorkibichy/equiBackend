package com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory;

import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.ProcessorCategory;
import com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory.DTO.ProcessorSubCategoryRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/processor/processorSubcategory")
@Tag(name = "Processor Sub Category")
@RequiredArgsConstructor
public class ProcessorSubCategoryController {
    private final ProcessorSubCategoryService processorSubCategoryService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> createProcessorSubCategory(@RequestBody ProcessorSubCategoryRequest
                                                                    processorSubCategoryRequest) {
        return ResponseEntity.ok(processorSubCategoryService.addProcessorSubCategory(processorSubCategoryRequest));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllProcessorSubCategories() {
        return ResponseEntity.ok(processorSubCategoryService.getAllProcessorSubCategories());
    }

    @GetMapping("get/by/category/id/{processorCategoryId}")
    public ResponseEntity<?> getSubCategoriesByCategory(@PathVariable("processorCategoryId") Long processorCategoryId) {
        ProcessorCategory processorCategory = new ProcessorCategory();
        processorCategory.setId(processorCategoryId);
        return ResponseEntity.ok(processorSubCategoryService.getByProcessorCategory(processorCategory));
    }

    @GetMapping("/get/by/id/{processorSubCategoryId}")
    public ResponseEntity<?> getProcessorSubCategoryById(@PathVariable("processorSubCategoryId")
                                                             Long processorSubCategoryId){
        return ResponseEntity.ok(processorSubCategoryService.getProcessorSubCategoryById(processorSubCategoryId));
    }

    @PutMapping("/update/{processorSubCategoryId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateProcessorSubCategory(
            @RequestBody ProcessorSubCategoryRequest processorSubCategoryRequest,
            @PathVariable("processorSubCategoryId") Long processorSubCategoryId){
        return ResponseEntity.ok(processorSubCategoryService.updateProcessorSubCategory(
                processorSubCategoryRequest, processorSubCategoryId));
    }
    @DeleteMapping("/delete/{processorSubCategoryId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteProcessorSubCategory(@PathVariable("processorSubCategoryId")
                                                            Long processorSubCategoryId){
        return ResponseEntity.ok(processorSubCategoryService.deleteProcessorSubCategory(processorSubCategoryId));
    }
}
