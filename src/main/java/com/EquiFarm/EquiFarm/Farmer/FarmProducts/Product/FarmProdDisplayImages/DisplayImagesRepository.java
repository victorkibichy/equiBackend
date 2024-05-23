package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayImagesRepository extends JpaRepository<DisplayImages,Long> {
    Optional<DisplayImages> findByDeletedFlagAndId(Character deleteFlag, Long id);
}
