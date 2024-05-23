package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessChekout;

import com.EquiFarm.EquiFarm.Checkout.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgriBusinessCheckoutRepository extends JpaRepository<AgriBusinessCheckout, Long> {
    List<AgriBusinessCheckout> findByDeletedFlag(Character deleteFlag);
    Optional<AgriBusinessCheckout> findByDeletedFlagAndId(Character deleteFlag, Long id);

    Optional<AgriBusinessCheckout> findByDeletedFlagAndAgriBusinessOrderId(Character deleteFlag, Long agriBusinessOrderId);
}
