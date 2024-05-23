package com.EquiFarm.EquiFarm.DigitalTraining;

import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DigitalTrainingRepository extends JpaRepository<DigitalTraining,Long> {
//    Optional<DigitalTraining> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);

    Optional<DigitalTraining> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<DigitalTraining> findByDeletedFlag(Character deleteFlag);

    Boolean existsByTrainingName(String trainingName);

    Optional<DigitalTraining> findByIdAndDeletedFlag(Long digitalTrainingId, Character no);


}
