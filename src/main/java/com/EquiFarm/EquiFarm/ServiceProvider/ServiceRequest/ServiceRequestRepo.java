package com.EquiFarm.EquiFarm.ServiceProvider.ServiceRequest;

import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.ProvidedServices;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRequestRepo extends JpaRepository<ServiceRequest, Long> {
    Optional<ServiceRequest> findByIdAndDeletedFlag(Long id, Character deletedFlag);
    List<ServiceRequest> findByProvidedServices(ProvidedServices serviceProvider);
    List<ServiceRequest> findByDeletedFlag(Character deletedFlag);
}
