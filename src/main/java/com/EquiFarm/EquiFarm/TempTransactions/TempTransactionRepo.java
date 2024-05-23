package com.EquiFarm.EquiFarm.TempTransactions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TempTransactionRepo extends JpaRepository<TempTransaction, Long> {
    List<TempTransaction> findByDeletedFlag(Character deletedFlag);
//    List<TempTransaction> findByDeletedFlagAndBuyerUserId(Character deletedFlag, Long buyerUserId);
//    List<TempTransaction> findByDeletedFlagAndSellerUserId(Character deletedFlag, Long sellerUserId);

    @Query("SELECT e FROM TempTransaction e WHERE e.buyerUserId = :userId OR e.sellerUserId = :userId")
    List<TempTransaction> findByBuyerUserIdOrSellerUserId(@Param("userId") Long userId);
}
