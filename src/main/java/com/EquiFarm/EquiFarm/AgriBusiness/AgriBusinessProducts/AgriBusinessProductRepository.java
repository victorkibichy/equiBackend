package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgriBusinessProductRepository extends JpaRepository<AgriBusinessProduct,Long> {
    Optional<AgriBusinessProduct> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<AgriBusinessProduct> findByDeletedFlag(Character deleteFlag);

    List<AgriBusinessProduct> findByAgriBusinessId(Long agriBusinessId);
}