package com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeOfServicesRepository extends JpaRepository<TypeOfServices,Long>{
 Optional<TypeOfServices> findByTypeOfServiceAndDeletedFlag(String typeOfService, Character deleteFlag);
 Optional<TypeOfServices> findByDeletedFlagAndId(Character deleteId, Long id);
 List<TypeOfServices> findByDeletedFlag(Character deleteFlag);
 Boolean existsByTypeOfService(String typeOfService);
}
