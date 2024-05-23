package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.TypeOfAgriBusinessProduct;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TypeOfAgriBusinessProductRepository extends JpaRepository<TypeOfAgriBusinessProduct,Long> {

    Optional<TypeOfAgriBusinessProduct> findByDeletedFlagAndId(Character deleteFlag, Long id);
    List<TypeOfAgriBusinessProduct> findByDeletedFlag(Character deleteFlag);
    Optional<TypeOfAgriBusinessProduct> findByDeletedFlagAndTypeOfAgriBusinessProduct(Character deleteFlag,String typeOfAgriBusinessProduct);
    Boolean existsByTypeOfAgriBusinessProduct(String typeOfAgriBusinessProduct);
}

