package com.EquiFarm.EquiFarm.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByNationalId(String nationalId);
    Optional<User> findByEmailOrNationalId(String email, String nationalId);
    Optional<User> findUserByPhoneNo(String phone);
    Optional<User> findByEmailOrPhoneNo(String email, String phoneNo);
    Optional<User> findByDeletedFlagAndId(Character deleteFlag, Long id);
    List<User> findByDeletedFlag(Character deletedFlag);
    
  
  }