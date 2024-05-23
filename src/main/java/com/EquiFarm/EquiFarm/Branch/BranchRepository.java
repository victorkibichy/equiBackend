package com.EquiFarm.EquiFarm.Branch;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findByDeletedFlag(Character deleteFlag);
    Optional<Branch> findByDeletedFlagAndId(Character deleteFlag, Long id);
    Optional<Branch> findByDeletedFlagAndSolId(Character deleteFlag, String solId);
    Optional<Branch> findByDeletedFlagAndBranchName(Character deleteFlag, String branchName);
}
