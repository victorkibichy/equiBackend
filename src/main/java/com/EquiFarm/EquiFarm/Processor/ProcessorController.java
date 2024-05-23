package com.EquiFarm.EquiFarm.Processor;

import com.EquiFarm.EquiFarm.Processor.DTO.ProcessorRequest;
import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.ProcessorCategory;
import com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory.ProcessorSubCategory;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/processor")
@Tag(name = "Processors")
@Slf4j
@RequiredArgsConstructor
public class ProcessorController {
    private final ProcessorService processorService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('processor:read')")
    public ResponseEntity<?> findProcessorProfile() {
        return ResponseEntity.ok(processorService.fetchProcessorProfile());
    }

    @GetMapping("/get/by/userId/{userId}")
    public ResponseEntity<?> getProcessorById(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(processorService.getProcessorByUserId(id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllProcessors() {
        return ResponseEntity.ok(processorService.getAllProcessors());
    }

    @GetMapping("/get/by/value/chain/{valueChainId}")
    public ResponseEntity<?> fetchProcessorsByValueChain(@PathVariable("valueChainId") Long valueChainId) {
        return ResponseEntity.ok(processorService.getAllProcessorsInValueChain(valueChainId));
    }

    @GetMapping("/get/by/category/id/{categoryId}")
    public ResponseEntity<?> getByCategory(@PathVariable("categoryId") Long categoryId) {
        ProcessorCategory processorCategory = new ProcessorCategory();
        processorCategory.setId(categoryId);
        return ResponseEntity.ok(processorService.getByProcessorCategory(processorCategory));
    }

    @GetMapping("/get/by/subcategory/id/{subCategoryId}")
    public ResponseEntity<?> getBySubCategory(@PathVariable("processorSubCategoryId") Long processorSubCategoryId) {
        ProcessorSubCategory processorSubCategory = new ProcessorSubCategory();
        processorSubCategory.setId(processorSubCategoryId);
        return ResponseEntity.ok(processorService.getByProcessorSubCategory(processorSubCategory));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('processor:update')")
    public ResponseEntity<?> processorProfileUpdate(
            @RequestBody ProcessorRequest processorRequest) {
        return ResponseEntity.ok(processorService.processorProfileUpdate(processorRequest));
    }

    @PutMapping("/update/by/processorId/{processorId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateByProcessorId(@RequestBody ProcessorRequest processorRequest, @PathVariable("processorId") Long processorId) {
        return ResponseEntity.ok(processorService.updateProcessorByProcessorId(processorRequest, processorId));
    }

    @PutMapping("/update/by/userId/{userId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateByUserId(@RequestBody ProcessorRequest processorRequest, @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(processorService.updateProcessorByUserId(processorRequest, userId));
    }

    @DeleteMapping("/delete/{processorId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteProcessor(
            @PathVariable("processorId") Long processorId) {
        return ResponseEntity.ok(processorService.deleteProcessor(processorId));
    }
}
