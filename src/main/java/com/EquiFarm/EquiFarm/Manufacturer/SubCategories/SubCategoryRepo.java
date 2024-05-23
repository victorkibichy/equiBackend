package com.EquiFarm.EquiFarm.Manufacturer.SubCategories;

import com.EquiFarm.EquiFarm.Manufacturer.Categories.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepo extends JpaRepository<SubCategory, Long> {
    Optional<SubCategory> findBySubCategoryAndDeletedFlag(String subCategory, Character deleteFlag);
    Optional<SubCategory> findByIdAndDeletedFlag(Long id, Character deleteId);
    List<SubCategory> findByCategory(Category category);
    List<SubCategory> findByDeletedFlag(Character deleteFlag);
}
