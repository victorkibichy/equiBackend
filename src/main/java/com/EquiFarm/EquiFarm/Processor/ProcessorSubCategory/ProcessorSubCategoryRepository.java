package com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory;

import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.ProcessorCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProcessorSubCategoryRepository extends JpaRepository<ProcessorSubCategory, Long> {
    Optional<ProcessorSubCategory> findByProcessorSubCategoryAndDeletedFlag(String processorSubCategory, Character deleteFlag);
    Optional<ProcessorSubCategory> findByIdAndDeletedFlag(Long id, Character deleteId);
    List<ProcessorSubCategory> findByProcessorCategory(ProcessorCategory processorCategory);
    List<ProcessorSubCategory> findByDeletedFlag(Character deleteFlag);
}
