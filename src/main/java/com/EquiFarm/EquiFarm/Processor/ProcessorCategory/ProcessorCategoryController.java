package com.EquiFarm.EquiFarm.Processor.ProcessorCategory;

import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.DTO.ProcessorCategoryRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/processor/processorCategory")
@Tag(name = "Processor Category")
@RequiredArgsConstructor
public class ProcessorCategoryController {
    private final ProcessorCategoryService processorCategoryService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> createProcessorCategory(@RequestBody ProcessorCategoryRequest processorCategoryRequest) {
        return ResponseEntity.ok(processorCategoryService.addProcessorCategory(processorCategoryRequest));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllProcessorCategories() {
        return ResponseEntity.ok(processorCategoryService.getAllProcessorCategories());
    }

    @GetMapping("/get/by/id/{processorCategoryId}")
    public ResponseEntity<?> getProcessorCategoryById(@PathVariable("processorCategoryId")
                                                          Long processorCategoryId){
        return ResponseEntity.ok(processorCategoryService.getProcessorCategoryById(processorCategoryId));
    }

    @PutMapping("/update/{processorCategoryId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateProcessorCategory(@RequestBody ProcessorCategoryRequest processorCategoryRequest,
                                            @PathVariable("processorCategoryId") Long processorCategoryId){
        return ResponseEntity.ok(processorCategoryService.updateProcessorCategory(processorCategoryRequest,
                processorCategoryId));
    }
    @DeleteMapping("/delete/{processorCategoryId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteProcessorCategory(@PathVariable("processorCategoryId") Long processorCategoryId){
        return ResponseEntity.ok(processorCategoryService.deleteProcessorCategory(processorCategoryId));
    }
}
