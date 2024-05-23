package com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices;

import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProvidedServicesRepo extends JpaRepository<ProvidedServices, Long> {
    Optional<ProvidedServices> findByIdAndDeletedFlag(Long id, Character deletedFlag);
    List<ProvidedServices> findByDeletedFlag(Character  deletedFlag);
}
