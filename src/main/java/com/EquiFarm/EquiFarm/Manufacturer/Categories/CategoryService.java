package com.EquiFarm.EquiFarm.Manufacturer.Categories;

import com.EquiFarm.EquiFarm.Manufacturer.Categories.DTO.CategoryRequest;
import com.EquiFarm.EquiFarm.Manufacturer.Categories.DTO.CategoryResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

    public ApiResponse<?> addCategory(CategoryRequest categoryRequest) {
        try {
            Optional<Category> categoryOptional = categoryRepo.findByCategoryAndDeletedFlag(categoryRequest.getCategory(), Constants.NO);
            if (categoryOptional.isPresent()) {
                return new ApiResponse<>("Category already exists.", null, HttpStatus.BAD_REQUEST.value());
            }
            if (categoryRequest.getCategory() == null) {
                return new ApiResponse<>("Category is required.", null, HttpStatus.BAD_REQUEST.value());
            }
            Category category = new Category();
            category.setCategory(categoryRequest.getCategory());
            category.setDescription(category.getDescription());
            Category savedCategory = categoryRepo.save(category);
            CategoryResponse categoryResponse = modelMapper.map(savedCategory, CategoryResponse.class);

            return new ApiResponse<>("Category added successfully.", categoryResponse,
                    HttpStatus.CREATED.value());

        } catch (Exception e) {
            log.error("Adding new Category Error: ", e);
            return new ApiResponse<>("An error occurred while adding a manufacturer category.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> findAllCategories(){
        try {
            List<Category> categoryList = categoryRepo.findByDeletedFlag(Constants.NO);

            List<CategoryResponse> categoryResponse = categoryList.stream()
                    .map(category -> modelMapper.map(category, CategoryResponse.class)).toList();

            return new ApiResponse<>("Manufacturer categories fetched Successfully.", categoryResponse,
                    HttpStatus.OK.value());

        } catch (Exception e){
            log.error("Fetching all Categories Error: ", e);
            return new ApiResponse<>("An Error occurred while fetching all categories.", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> findCategoryById(Long id){
        try {
            Optional<Category> categoryOptional = categoryRepo.findByIdAndDeletedFlag(id, Constants.NO);

            if (categoryOptional.isPresent()) {
                Category category = categoryOptional.get();

                CategoryResponse categoryResponse = modelMapper.map(category,
                        CategoryResponse.class);

                return new ApiResponse<>("Category successfully fetched.", categoryResponse,
                        HttpStatus.FOUND.value());
            } else {
                return new ApiResponse<>("Category not found.", null, HttpStatus.NOT_FOUND.value());
            }

        }catch (Exception e){
            log.info("Find category by Id Error: ", e);
            return new ApiResponse<>("An Error occurred while fetching a category by its Id", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

//    public ApiResponse<?> findCategoryAndSubCategories(){
//
//    }
    public ApiResponse<?> updateCategory(CategoryRequest categoryRequest, Long categoryId){
        try {
            Optional<Category> categoryOptional = categoryRepo.findByIdAndDeletedFlag(categoryId, Constants.NO);
            if (categoryOptional.isPresent()) {
                Category category = categoryOptional.get();
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(categoryRequest.getDescription(),
                        category::setDescription,
                        category::getDescription);
                if (categoryRequest.getCategory() != null
                        && categoryRequest.getCategory().length() > 0
                        && !Objects.equals(category.getCategory(),
                        categoryRequest.getCategory())) {
                    Optional<Category> categoryOptional1 = categoryRepo
                            .findByCategoryAndDeletedFlag(categoryRequest.getCategory(), Constants.NO);

                    if (categoryOptional1.isPresent()) {
                        return new ApiResponse<>("Category already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    category.setCategory(categoryRequest.getCategory());

                }
                Category updatedCategory = categoryRepo.save(category);

                CategoryResponse categoryResponse = modelMapper.map(updatedCategory,
                        CategoryResponse.class);
                return new ApiResponse<>("Category updated successfully", categoryResponse, HttpStatus.OK.value());

            }else {
                    return new ApiResponse<>("Category not found", null, HttpStatus.NOT_FOUND.value());
                }
        } catch (Exception e){
            log.info("Updating Category Error: ", e);
            return new ApiResponse<>("Error updating a category", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> deleteCategory(Long id){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Category> categoryOptional = categoryRepo.findByIdAndDeletedFlag(id, Constants.NO);

            if (categoryOptional.isPresent()) {
                Category category = categoryOptional.get();
                category.setDeletedAt(LocalDateTime.now());
                category.setDeletedBy(currentUser);
                category.setDeletedFlag(Constants.YES);
                categoryRepo.save(category);

                return new ApiResponse<>("Category successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Category not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e){
            log.info("Deleting a category error: ", e);
            return new ApiResponse<>("Error deleting a category", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}



