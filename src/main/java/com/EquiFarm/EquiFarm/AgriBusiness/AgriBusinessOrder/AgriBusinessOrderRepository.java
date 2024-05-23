package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgriBusinessOrderRepository extends JpaRepository<AgriBusinessOrder, Long> {
    Optional<AgriBusinessOrder> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<AgriBusinessOrder> findByDeletedFlagAndUserIdAndIsOrderedAndIsPaid(Character deleteFlag, Long userId, boolean isOrdered, boolean isPaid);

    List<AgriBusinessOrder> findByDeletedFlag(Character deleteFlag);

    List<AgriBusinessOrder> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);

    Optional<AgriBusinessOrder> findByDeletedFlagAndIdAndUserIdAndAgriBusinessOrderStatusAndIsOrderedAndIsPaid(Character deleteFlag, Long id, Long userId, AgriBusinessOrderStatus agriBusinessOrderStatus, boolean isOrdered, boolean isPaid);
    List<AgriBusinessOrder> findByDeletedFlagAndAgriBusinessOrderStatusIn(Character deletedFlag, List<AgriBusinessOrderStatus> agriBusinessOrderStatuses);
    List<AgriBusinessOrder> findByDeletedFlagAndAgriBusinessOrderStatusAndIsOrderedAndIsPaid(Character deletedFlag, AgriBusinessOrderStatus agriBusinessOrderStatus, boolean isOrdered, boolean isPaid);

}


