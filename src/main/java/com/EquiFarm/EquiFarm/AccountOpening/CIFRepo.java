package com.EquiFarm.EquiFarm.AccountOpening;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CIFRepo extends JpaRepository<CIF, Long> {
    Optional<CIF> findByNationalIdAndFirstName(String nationalId, String firstName);
    Boolean existsByNationalId(String nationalId);
}
