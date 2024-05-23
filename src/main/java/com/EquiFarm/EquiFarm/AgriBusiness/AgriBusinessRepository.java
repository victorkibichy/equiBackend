package com.EquiFarm.EquiFarm.AgriBusiness;

import java.util.List;
import java.util.Optional;

import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AgriBusinessRepository extends JpaRepository<AgriBusiness, Long> {
    Optional<AgriBusiness> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);

    Optional<AgriBusiness> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<AgriBusiness> findByDeletedFlag(Character deleteFlag);

    List<AgriBusiness> findByValueChainsContaining(ValueChain valueChain);

    Optional<AgriBusiness> findByIdNumber(String idNumber);
    Optional<AgriBusiness> findFirstByOrderByAgribusinessCodeDesc();
}
