package com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct;

import com.EquiFarm.EquiFarm.Warehouse.Warehouse;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.QualityCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseProductRepo extends JpaRepository<WarehouseProduct, Long> {
    Optional<WarehouseProduct> findByIdAndDeletedFlag(Long id, Character deletedFlag);
    List<WarehouseProduct> findByDeletedFlag(Character deletedFlag);
    List<WarehouseProduct> findByWarehouseAndDeletedFlag(Warehouse warehouse, Character deletedFlag);
    List<WarehouseProduct> findByVerifiedAndAndWarehouseAndDeletedFlag(boolean verified,Warehouse warehouse, Character deleteFlag);
    Optional<WarehouseProduct> findByVerifiedAndIdAndDeletedFlag(boolean verified, Long id, Character deletedFlag);
    List<WarehouseProduct> findByVerifiedAndAvailableAndDeletedFlag(boolean verified, boolean available, Character deletedFlag);
    List<WarehouseProduct> findByVerifiedAndDeletedFlag(boolean verified, Character deletedFlag);
    List<WarehouseProduct> findAllByVerifiedAndDeletedFlagOrderByExpiryDateAsc(boolean verified, Character deletedFlag);
    List<WarehouseProduct> findByQualityCheckedAndDeletedFlag(boolean qualityChecked, Character deletedFlag);
    List<WarehouseProduct> findByQualityCheckedAndStatusAndDeletedFlag(boolean qualityChecked, WHProductStatus status, Character deletedFLag);
    @Query("SELECT q FROM WarehouseProduct wp JOIN wp.qualityChecks q WHERE wp.id = :warehouseProductId AND wp.deletedFlag = :deletedFlag")
    List<QualityCheck> findQualityChecksByIdAndDeletedFlag(Long warehouseProductId, Character deletedFlag);
}
