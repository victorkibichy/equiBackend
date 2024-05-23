package com.EquiFarm.EquiFarm.Customer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);

    Optional<Customer> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<Customer> findByDeletedFlag(Character deleteFlag);

    Optional<Customer> findByIdNumber(String idNumber);
}
