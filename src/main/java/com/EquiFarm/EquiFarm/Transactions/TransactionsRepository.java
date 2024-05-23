package com.EquiFarm.EquiFarm.Transactions;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EquiFarm.EquiFarm.Checkout.Checkout;


@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    Optional<Transactions> findByDeletedFlagAndId(Character deleteFlag, Long id);

    Optional<Transactions> findByDeletedFlagAndCheckout(Character deletedFlag, Checkout checkout);
    

    List<Transactions> findByDeletedFlag(Character deletedFlag);

}
