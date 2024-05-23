package com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Long>{
    Optional<ProductCategory> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<ProductCategory> findByDeletedFlag(Character deleteFlag);
    
    Optional<ProductCategory> findByDeletedFlagAndProductCategory(Character deleteFlag,String productCategory);
}
