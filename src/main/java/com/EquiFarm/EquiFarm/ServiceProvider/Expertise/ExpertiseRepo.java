package com.EquiFarm.EquiFarm.ServiceProvider.Expertise;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpertiseRepo extends JpaRepository<Expertise, Long> {
    Boolean existsByExpertiseName(String expertiseName);
    Optional<Expertise> findByIdAndDeletedFlag(Long id, Character deletedFlag);
    List<Expertise> findByDeletedFlag(Character deletedFlag);
}
