package com.EquiFarm.EquiFarm.Staff;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);
    Optional<Staff> findByDeletedFlagAndId(Character deleteFlag, Long id);
    List<Staff> findByDeletedFlag(Character deleteFlag);
    Optional<Staff> findByIdNumber(String idNumber);
}