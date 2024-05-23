package com.EquiFarm.EquiFarm.Insurance.TypeOfInsurance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TypeOfInsuranceRepository extends JpaRepository<TypeOfInsurance,Long> {
    Optional<TypeOfInsurance> findByTypeOfInsuranceAndDeletedFlag(String typeOfInsurance, Character deleteFlag);
    Optional<TypeOfInsurance> findByDeletedFlagAndId(Character deleteId, Long id);
    List<TypeOfInsurance> findByDeletedFlag(Character deleteFlag);
}