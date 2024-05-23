package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Inventory;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import com.EquiFarm.EquiFarm.utils.Constants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.scanner.Constant;

import java.util.Optional;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Long> {
  Optional<Inventory> findByIdAndDeletedFlag(Long id, Character deletedFlag);
  Optional<Inventory> findByFarmProducts(FarmProducts farmProducts);
}
