package com.EquiFarm.EquiFarm.Processor.ProcessorCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessorCategoryRepository extends JpaRepository<ProcessorCategory, Long> {
    Optional<ProcessorCategory> findByProcessorCategoryAndDeletedFlag(String processorCategory, Character deleteFlag);
    List<ProcessorCategory> findByDeletedFlag(Character deleteFlag);
    Optional<ProcessorCategory> findByIdAndDeletedFlag(Long id, Character deleteId);
}
