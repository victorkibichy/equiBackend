package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.Inventory;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgriInventoryRepo extends JpaRepository<AgriInventory, Long> {
  Optional<AgriInventory> findByIdAndDeletedFlag(Long id, Character deletedFlag);
  Optional<AgriInventory> findByAgriBusinessProduct(AgriBusinessProduct agriBusinessProduct);
}
