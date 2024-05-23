package com.EquiFarm.EquiFarm.Rating.AgriBusinessRating;

import com.EquiFarm.EquiFarm.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgriBusinessRatingRepository extends JpaRepository<AgriBusinessRating, Long> {
    List<AgriBusinessRating> findByDeletedFlag(Character deleteFlag);
    Optional<AgriBusinessRating> findByDeletedFlagAndId(Character deleteFlag, Long id);
    Optional<AgriBusinessRating> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);
    List<AgriBusinessRating> findByUser(User user);
}
