package com.EquiFarm.EquiFarm.Rating.DriverRating;

import com.EquiFarm.EquiFarm.Driver.Driver;
import com.EquiFarm.EquiFarm.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRatingRepository extends JpaRepository<DriverRating,Long> {
    List<DriverRating> findByDeletedFlag(Character deleteFlag);
    Optional<DriverRating> findByDeletedFlagAndId(Character deleteFlag, Long id);
    Optional<DriverRating> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);
    List<DriverRating> findByUser(User user);
}
