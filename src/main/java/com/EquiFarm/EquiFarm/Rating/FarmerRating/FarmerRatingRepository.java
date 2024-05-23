package com.EquiFarm.EquiFarm.Rating.FarmerRating;

import com.EquiFarm.EquiFarm.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FarmerRatingRepository extends JpaRepository<FarmerRating,Long> {
    List<FarmerRating> findByDeletedFlag(Character deleteFlag);
    Optional<FarmerRating> findByDeletedFlagAndId(Character deleteFlag, Long id);
    Optional<FarmerRating> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);
    List<FarmerRating> findByUser(User user);
    List<FarmerRating> findByFarmerIdAndDeletedFlag(Long id, Character deleteFlag);

}
