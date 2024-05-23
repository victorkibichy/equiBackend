package com.EquiFarm.EquiFarm.FarmTech;

import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FarmTechRepository extends JpaRepository<FarmTech, Long> {
    Optional<FarmTech> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);

    Optional<FarmTech> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<FarmTech> findByDeletedFlag(Character deleteFlag);

    List<FarmTech> findByValueChainsContaining(ValueChain valueChain);

    Optional<FarmTech> findByIdNumber(String idNumber);
    Optional<FarmTech> findFirstByOrderByFarmTechCodeDesc();
}


