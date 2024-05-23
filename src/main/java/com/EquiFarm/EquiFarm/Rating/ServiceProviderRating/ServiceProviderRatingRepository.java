package com.EquiFarm.EquiFarm.Rating.ServiceProviderRating;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceProviderRatingRepository extends JpaRepository<ServiceProviderRating,Long> {
    List<ServiceProviderRating> findByDeletedFlag(Character deleteFlag);
    List<ServiceProviderRating> findByDeletedFlagAndServiceProviderId(Character deleteFlag, Long serviceProviderId);
    List<ServiceProviderRating> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);
    Optional<ServiceProviderRating> findByDeletedFlagAndId(Character deleteFlag, Long id);
}