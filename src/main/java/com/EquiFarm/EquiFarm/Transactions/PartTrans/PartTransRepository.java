package com.EquiFarm.EquiFarm.Transactions.PartTrans;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartTransRepository extends JpaRepository<PartTrans, Long> {
    Optional<PartTrans> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<PartTrans> findByDeletedFlag(Character deleteFlag);
}
