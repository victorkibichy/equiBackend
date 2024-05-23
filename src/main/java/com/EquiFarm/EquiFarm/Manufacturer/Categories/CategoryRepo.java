package com.EquiFarm.EquiFarm.Manufacturer.Categories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryAndDeletedFlag(String category, Character deleteFlag);
    Optional<Category> findByIdAndDeletedFlag(Long id, Character deleteId);
    List<Category> findByDeletedFlag(Character deleteFlag);
}
