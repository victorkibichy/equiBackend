package com.EquiFarm.EquiFarm.Driver;

import java.util.List;
import java.util.Optional;

import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends  JpaRepository<Driver,Long> {
    Optional<Driver> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);
    Optional<Driver> findByDeletedFlagAndId(Character deleteId, Long id);
    
    List<Driver> findByDeletedFlag(Character deleteFlag);
    Optional<Driver> findByIdNumber(String idNumber);

    List<Driver> findByValueChainsContaining(ValueChain valueChain);
    Optional<Driver> findFirstByOrderByDriverCodeDesc();


}
