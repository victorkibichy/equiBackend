package com.EquiFarm.EquiFarm.Order;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByDeletedFlagAndId(Character deleteFlag, Long id);
    List<Order> findByDeletedFlagAndUserIdAndIsOrderedAndIsPaid(Character deleteFlag, Long userId,boolean isOrdered,boolean isPaid);
    List<Order> findByDeletedFlag(Character deleteFlag);
    List<Order> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);
    Optional<Order> findByDeletedFlagAndIdAndUserIdAndOrderStatusAndIsOrderedAndIsPaid(Character deleteFlag, Long id,Long userId,OrderStatus orderStatus,boolean isOrdered,boolean isPaid);
    List<Order> findByDeletedFlagAndOrderStatusAndIsOrderedAndIsPaid(Character deletedFlag, OrderStatus orderStatus,boolean isOrdered,boolean isPaid);
    List<Order> findByDeletedFlagAndOrderStatusInAndIsOrderedAndIsPaid(Character deletedFlag, List<OrderStatus> statuses, boolean isOrdered, boolean isPaid);
    List<Order> findByDeletedFlagAndOrderStatusIn(Character deletedFlag, List<OrderStatus> statuses);
}
