package com.EquiFarm.EquiFarm.Warehouse;

import com.EquiFarm.EquiFarm.Manufacturer.Manufacturer;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepo extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByIdAndDeletedFlag(Long id, Character deletedFlag);
    Optional<Warehouse> findByDeletedFlagAndUserId(Character deletedFlag, Long userId);
    List<Warehouse> findByDeletedFlag(Character deletedFlag);
    List<Warehouse> findByVacant(boolean isVacant);
    List<Warehouse> findByCapacity(Double capacity);
    Optional<Warehouse> findByIdNumber(String phoneNumber);
    List<Warehouse> findByValueChainsContaining(ValueChain valueChain);
    Optional<Warehouse> findFirstByOrderByWarehouseCodeDesc();
}
