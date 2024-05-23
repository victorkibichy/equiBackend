package com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts;

import java.util.List;
import java.util.Optional;

import com.EquiFarm.EquiFarm.Manufacturer.Categories.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeOfProductsRepository extends JpaRepository<TypesOfProducts,Long>{
    Optional<TypesOfProducts> findByDeletedFlagAndId(Character deleteFlag, Long id);
    List<TypesOfProducts> findByDeletedFlag(Character deleteFlag);
    Optional<TypesOfProducts> findByDeletedFlagAndTypeOfProduct(Character deleteFlag,String typeOfProduct);
    Boolean existsByTypeOfProduct(String typeOfProduct);
}
