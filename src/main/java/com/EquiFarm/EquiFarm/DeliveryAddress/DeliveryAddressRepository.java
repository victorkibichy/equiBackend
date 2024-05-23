package com.EquiFarm.EquiFarm.DeliveryAddress;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    Optional<DeliveryAddress> findByDeletedFlagAndId(Character deleteFlag, Long id);

    Optional<DeliveryAddress> findByDeletedFlagAndUserIdAndId(Character deleteFlag, Long userId, Long id);

    Optional<DeliveryAddress> findByDeletedFlagAndUserIdAndAddressName(Character deleteFlag, Long userId,
            String addressName);

    List<DeliveryAddress> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);

    List<DeliveryAddress> findByDeletedFlagAndUserIdAndIsDefaultAddress(Character deleteFlag, Long userId,
            Boolean isDefaultAddress);

    List<DeliveryAddress> findByDeletedFlag(Character deleteFlag);
}
