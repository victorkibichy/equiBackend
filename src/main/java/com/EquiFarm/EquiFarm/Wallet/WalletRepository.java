package com.EquiFarm.EquiFarm.Wallet;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet,Long>{
    Optional<Wallet> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);
    
    Optional<Wallet> findByDeletedFlagAndId(Character deleteId, Long id);
    
    List<Wallet> findByDeletedFlag(Character deleteFlag);
}
