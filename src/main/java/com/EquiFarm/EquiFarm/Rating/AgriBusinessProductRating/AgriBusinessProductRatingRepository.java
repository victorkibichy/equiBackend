package com.EquiFarm.EquiFarm.Rating.AgriBusinessProductRating;

import com.EquiFarm.EquiFarm.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgriBusinessProductRatingRepository extends JpaRepository<AgriBusinessProductRating,Long> {
    List<AgriBusinessProductRating> findByDeletedFlag(Character deleteFlag);
    Optional<AgriBusinessProductRating> findByDeletedFlagAndId(Character deleteFlag, Long id);
    Optional<AgriBusinessProductRating> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);
    List<AgriBusinessProductRating> findByUser(User user);
}




