package com.EquiFarm.EquiFarm.EscrowWallet;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EquiFarm.EquiFarm.Checkout.Checkout;

public interface EscrowWalletRepository extends JpaRepository<EscrowWallet, Long> {

    Optional<EscrowWallet> findByDeletedFlagAndId(Character deleteFlag, Long id);

    Optional<EscrowWallet> findByDeletedFlagAndSenderWalletIdAndReceiverWalletIdAndCheckoutIdAndIsOpened(
            Character deletedFlag,
            Long senderWalletId,
            Long receiverWalletId,
            Long checkoutId,
            boolean isOpened);

    Optional<EscrowWallet> findByDeletedFlagAndCheckoutId(Character deletedFlag, Long checkoutId);
    Optional<EscrowWallet> findByDeletedFlagAndCheckout(Character deletedFlag, Checkout checkout);

    List<EscrowWallet> findByDeletedFlagAndStatus(Character deletedFlag, EscrowStatus status);

    List<EscrowWallet> findByDeletedFlagAndSenderWalletId(Character deletedFlag, Long senderWalletId);

    List<EscrowWallet> findByDeletedFlagAndReceiverWalletId(Character deletedFlag, Long receiverWalletId);

    List<EscrowWallet> findByDeletedFlagAndIsOpened(Character deletedFlag, boolean isOpened);

    List<EscrowWallet> findByDeletedFlag(Character deletedFlag);
}
