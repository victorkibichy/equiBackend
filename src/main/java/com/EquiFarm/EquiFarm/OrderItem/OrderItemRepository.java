package com.EquiFarm.EquiFarm.OrderItem;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<OrderItem> findByDeletedFlag(Character deleteFlag);

    List<OrderItem> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);


    // Optional<OrderItem> findByDeletedFlagAndFarmProductIdAndUserIdAndIsPaidAndIsOrdered(Character deleteFlag,
    //         Long farmProductId, Long userId, boolean isPaid, boolean isOrdered);
    Optional<OrderItem> findByDeletedFlagAndFarmProductIdAndUserIdAndIsPaidAndIsOrdered(
                Character deleteFlag, Long farmProductId, Long userId, boolean isPaid, boolean isOrdered
            );
            
}
