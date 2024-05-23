package com.EquiFarm.EquiFarm.EscrowWallet.Disputes;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EquiFarm.EquiFarm.EscrowWallet.EscrowWallet;
import com.EquiFarm.EquiFarm.user.User;

@Repository
public interface DisputesRepository extends JpaRepository<Dispute, Long>{
    Optional<Dispute> findByDeletedFlagAndId(Character deletedFlag, Long id);
    List<Dispute> findByDeletedFlagAndEscrowWallet(Character deletedFlag,EscrowWallet escrowWallet);

    List<Dispute> findByDeletedFlagAndInitiator(Character deletedFlag,User user);

}
