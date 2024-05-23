package com.EquiFarm.EquiFarm.AgriBusiness.TypeOfBusiness;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TypeOfBusinessRepository extends JpaRepository<TypeOfBusiness,Long>{
     Optional<TypeOfBusiness> findByTypeOfBusinessAndDeletedFlag(String typeOfService, Character deleteFlag);
 Optional<TypeOfBusiness> findByDeletedFlagAndId(Character deleteId, Long id);
 
 List<TypeOfBusiness> findByDeletedFlag(Character deleteFlag);
}
