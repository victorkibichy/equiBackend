package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrderItem;

import com.EquiFarm.EquiFarm.OrderItem.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgriBusinessOrderItemRepository extends JpaRepository<AgriBusinessOrderItem, Long> {
    List<AgriBusinessOrderItem> findByDeletedFlag(Character deleteFlag);

    Optional<AgriBusinessOrderItem> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<AgriBusinessOrderItem> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);

    Optional<AgriBusinessOrderItem> findByDeletedFlagAndAgriBusinessProductIdAndUserIdAndIsPaidAndIsOrdered(
            Character deleteFlag, Long farmProductId, Long userId, boolean isPaid, boolean isOrdered
    );

}
