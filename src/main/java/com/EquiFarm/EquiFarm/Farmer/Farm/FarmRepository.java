package com.EquiFarm.EquiFarm.Farmer.Farm;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    Optional<Farm> findByDeletedFlagAndId(Character deleteFlag, Long id);
    
    List<Farm> findByFarmersId(Long farmerId); 


    List<Farm> findByDeletedFlag(Character deleteFlag);
}
