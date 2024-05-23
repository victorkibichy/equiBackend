package com.EquiFarm.EquiFarm.ServiceProvider;

import java.util.List;
import java.util.Optional;

import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    Optional<ServiceProvider> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);

    Optional<ServiceProvider> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<ServiceProvider> findByDeletedFlag(Character deleteFlag);

    Boolean existsByIdNumber(String idNumber);
    Optional<ServiceProvider> findByUser(User user);
    Optional<ServiceProvider> findFirstByOrderBySpCodeDesc();
    List<ServiceProvider> findByValueChainsContaining(ValueChain valueChain);
}
