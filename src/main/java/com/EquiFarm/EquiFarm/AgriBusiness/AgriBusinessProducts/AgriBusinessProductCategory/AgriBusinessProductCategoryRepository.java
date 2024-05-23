package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgriBusinessProductCategoryRepository extends JpaRepository<AgriBusinessProductCategory,Long> {
    Optional<AgriBusinessProductCategory> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<AgriBusinessProductCategory> findByDeletedFlag(Character deleteFlag);

    Optional<AgriBusinessProductCategory> findByDeletedFlagAndProductCategory(Character deleteFlag,String agriBusinessProductCategory);
}

