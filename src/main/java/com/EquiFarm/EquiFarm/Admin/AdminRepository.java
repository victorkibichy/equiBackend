package com.EquiFarm.EquiFarm.Admin;

import java.util.List;
import java.util.Optional;

import com.EquiFarm.EquiFarm.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByDeletedFlagAndUserId(Character deleteFlag, Long userId);

    Optional<Admin> findByDeletedFlagAndId(Character deleteFlag, Long id);

    List<Admin> findByDeletedFlag(Character deleteFlag);

    Optional<Admin> findByIdNumber(String idNumber);

    Optional<Admin> findByUser(User user);
}
