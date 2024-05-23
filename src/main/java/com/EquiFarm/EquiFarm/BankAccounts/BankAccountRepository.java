package com.EquiFarm.EquiFarm.BankAccounts;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByDeletedFlagAndId(Character deleteFlag, Long id);

    Optional<BankAccount> findByDeletedFlagAndUserIdAndAccountNumber(Character deleteFlag, Long userId,
            String accountNumber);
            
    Optional<BankAccount> findByDeletedFlagAndUserId(Character deleteFlag,Long userId);

    List<BankAccount> findByDeletedFlag(Character deleteFlag);
}
