package com.EquiFarm.EquiFarm.Checkout;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    Optional<Checkout> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<Checkout> findByDeletedFlag(Character deleteFlag);

    Optional<Checkout> findByDeletedFlagAndOrderId(Character deleteFlag, Long orderId);
}
