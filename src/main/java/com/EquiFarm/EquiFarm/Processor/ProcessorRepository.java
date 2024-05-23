package com.EquiFarm.EquiFarm.Processor;

import com.EquiFarm.EquiFarm.Manufacturer.Manufacturer;
import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.ProcessorCategory;
import com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory.ProcessorSubCategory;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProcessorRepository extends JpaRepository<Processor, Long> {
    Optional<Processor> findByIdAndDeletedFlag(Long id, Character deletedFlag);
    Optional<Processor> findByUserIdAndDeletedFlag(Long userId, Character deletedFlag);

    List<Processor> findByDeletedFlag(Character deletedFlag);

    Optional<Processor> findByIdNumber(String idNumber);

    List<Processor> findByProcessorSubCategoryAndDeletedFlag(ProcessorSubCategory processorSubCategory, Character deletedFlag);
    List<Processor> findByProcessorCategoryAndDeletedFlag(ProcessorCategory processorCategory, Character deletedFlag);


    List<Processor> findByValueChainsContaining(ValueChain valueChain);
    Optional<Processor> findFirstByOrderByProcessorCodeDesc();

}

