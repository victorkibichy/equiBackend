package com.EquiFarm.EquiFarm.ValueChain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ValueChainRepo extends JpaRepository<ValueChain, Long> {
    Optional<ValueChain> findByIdAndDeletedFlag(Long id, Character deletedFlag);
    List<ValueChain> findByDeletedFlag(Character deletedFlag);
    Boolean existsByValueChain(String valueChain);
}
