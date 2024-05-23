package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessWishList;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgriBusinessWishListRepository extends JpaRepository<AgriBusinessWishList,Long> {
    List<AgriBusinessWishList> findByDeletedFlag(Character deleteFlag);

    Optional<AgriBusinessWishList> findByDeletedFlagAndId(Character deleteId, Long id);

    Optional<AgriBusinessWishList> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);






}
