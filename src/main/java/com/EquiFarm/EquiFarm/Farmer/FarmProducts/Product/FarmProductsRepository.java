package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FarmProductsRepository extends JpaRepository<FarmProducts,Long> {
    Optional<FarmProducts> findByDeletedFlagAndId(Character deleteFlag, Long id);

    Page<FarmProducts> findByDeletedFlag(Character deleteFlag, Pageable pageable);
    List<FarmProducts> findByDeletedFlag(Character deleteFlag);


    Page<FarmProducts> findAll(Pageable pageable);

    List<FarmProducts> findByFarmerId(Long farmerId);

//    List<FarmProducts> findByFarmerId(Long farmerId);

}
