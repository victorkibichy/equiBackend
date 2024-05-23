package com.EquiFarm.EquiFarm.WishList;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface WishListRepository extends JpaRepository<WishList,Long> {
    List<WishList> findByDeletedFlag(Character deleteFlag);

    Optional<WishList> findByDeletedFlagAndId(Character deleteId, Long id);

    Optional<WishList> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);


}
