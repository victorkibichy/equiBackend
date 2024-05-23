package com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.ProductCategory;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.WarehouseProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QualityCheckRepo extends JpaRepository<QualityCheck, Long> {
    Boolean existsByCheckName(String checkName);
    Optional<QualityCheck> findByIdAndDeletedFlag(Long id, Character deletedFlag);
    List<QualityCheck> findByDeletedFlag(Character deletedFlag);
}
