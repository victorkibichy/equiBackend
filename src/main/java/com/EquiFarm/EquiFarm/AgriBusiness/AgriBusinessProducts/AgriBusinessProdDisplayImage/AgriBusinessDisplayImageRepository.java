package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProdDisplayImage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgriBusinessDisplayImageRepository extends JpaRepository<AgriBusinessDisplayImages,Long>{
    Optional<AgriBusinessDisplayImages> findByDeletedFlagAndId(Character deleteFlag, Long id);

}
