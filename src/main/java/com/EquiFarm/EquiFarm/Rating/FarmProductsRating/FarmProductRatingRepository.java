package com.EquiFarm.EquiFarm.Rating.FarmProductsRating;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FarmProductRatingRepository extends JpaRepository<FarmProductRating, Long> {
    List<FarmProductRating> findByDeletedFlag(Character deleteFlag);
    List<FarmProductRating> findByDeletedFlagAndFarmProductId(Character deleteFlag, Long farmProductId);
    List<FarmProductRating> findByDeletedFlagAndUserId(Character deleteFlag, Long farmProductId);
    Optional<FarmProductRating> findByDeletedFlagAndId(Character deleteFlag, Long id);
    List<FarmProductRating> findByFarmProductIdAndDeletedFlag(Long id, Character deleteFlag);

}
