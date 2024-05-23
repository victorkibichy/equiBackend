package com.EquiFarm.EquiFarm.Manufacturer;

import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Manufacturer.Categories.Category;
import com.EquiFarm.EquiFarm.Manufacturer.SubCategories.SubCategory;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManufacturerRepo extends JpaRepository<Manufacturer, Long> {
    Optional<Manufacturer> findByIdAndDeletedFlag(Long id, Character deletedFlag);
    Optional<Manufacturer> findByUserIdAndDeletedFlag(Long userId, Character deletedFlag);

    List<Manufacturer> findByDeletedFlag(Character deletedFlag);

    Optional<Manufacturer> findByIdNumber(String idNumber);

    List<Manufacturer> findBySubCategoryAndDeletedFlag(SubCategory subCategory, Character deletedFlag);
    List<Manufacturer> findByCategoryAndDeletedFlag(Category category, Character deletedFlag);


    List<Manufacturer> findByValueChainsContaining(ValueChain valueChain);
    Optional<Manufacturer> findFirstByOrderByManufacturerCodeDesc();

}
