package com.EquiFarm.EquiFarm.Manufacturer.SubCategories;

import com.EquiFarm.EquiFarm.Manufacturer.Categories.Category;
import com.EquiFarm.EquiFarm.Manufacturer.Categories.CategoryRepo;
import com.EquiFarm.EquiFarm.Manufacturer.Categories.DTO.CategoryResponse;
import com.EquiFarm.EquiFarm.Manufacturer.SubCategories.DTO.SubCategoryRequest;
import com.EquiFarm.EquiFarm.Manufacturer.SubCategories.DTO.SubCategoryResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubCategoryService {
    private final SubCategoryRepo subCategoryRepo;

    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

    public ApiResponse<?> addSubCategory(SubCategoryRequest subCategoryRequest) {
        try {
            Optional<SubCategory> subCategoryOptional = subCategoryRepo.findBySubCategoryAndDeletedFlag(subCategoryRequest.getSubCategory(), Constants.NO);
            if (subCategoryOptional.isPresent()) {
                return new ApiResponse<>("Sub Category already exists.", null, HttpStatus.BAD_REQUEST.value());
            }
            if (subCategoryRequest.getSubCategory() == null) {
                return new ApiResponse<>("Sub Category is required.", null, HttpStatus.BAD_REQUEST.value());
            }
            Optional<Category> category = categoryRepo.findByIdAndDeletedFlag(subCategoryRequest.getCategoryId(), Constants.NO);
            System.out.println("Category: "+ category);
            if (category.isPresent()) {
                SubCategory subCategory = new SubCategory();
                subCategory.setSubCategory(subCategoryRequest.getSubCategory());
                subCategory.setDescription(subCategory.getDescription());
                subCategory.setCategory(category.get());
                SubCategory savedSubCategory = subCategoryRepo.save(subCategory);
                SubCategoryResponse subCategoryResponse = modelMapper.map(savedSubCategory, SubCategoryResponse.class);

                return new ApiResponse<>("SubCategory added successfully.", subCategoryResponse,
                        HttpStatus.CREATED.value());
            } else {
                return  new ApiResponse<>("Category Does not Exist", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("Adding new Sub Category Error: ", e);
            return new ApiResponse<>("An error occurred while adding a manufacturer sub category.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> findAllSubCategories(){
        try {
            List<SubCategory> subcategoryList = subCategoryRepo.findByDeletedFlag(Constants.NO);

            List<SubCategoryResponse> subCategoryResponse = subcategoryList.stream()
                    .map(subcategory -> modelMapper.map(subcategory, SubCategoryResponse.class)).toList();

            return new ApiResponse<>("Manufacturer sub categories fetched Successfully.", subCategoryResponse,
                    HttpStatus.OK.value());

        } catch (Exception e){
            log.error("Fetching all Sub Categories Error: ", e);
            return new ApiResponse<>("An Error occurred while fetching all sub categories.", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> findByCategory(Category Category){
        try {
            List<SubCategory> subCategoryList = subCategoryRepo.findByCategory(Category);
            if (subCategoryList.size() > 0){
                List<SubCategoryResponse> subCategoryResponse = subCategoryList.stream()
                        .map(subcategory -> modelMapper.map(subcategory, SubCategoryResponse.class)).toList();
                return new ApiResponse<>("Successfully Fetched Sub Categories", subCategoryResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Sub Categories not found", null, HttpStatus.OK.value());
            }

        } catch (Exception e){
            log.info("Error fetching sub categories:", e);
            return new ApiResponse<>("Error occurred when fetching sub categories", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> findSubCategoryById(Long id){
        try {
            Optional<SubCategory> subCategoryOptional = subCategoryRepo.findByIdAndDeletedFlag(id, Constants.NO);

            if (subCategoryOptional.isPresent()) {
                SubCategory subCategory = subCategoryOptional.get();

                SubCategoryResponse subCategoryResponse = modelMapper.map(subCategory,
                        SubCategoryResponse.class);

                return new ApiResponse<>("SubCategory successfully fetched.", subCategoryResponse,
                        HttpStatus.FOUND.value());
            } else {
                return new ApiResponse<>("Category not found.", null, HttpStatus.NOT_FOUND.value());
            }

        }catch (Exception e){
            log.info("Find subCategory by Id Error: ", e);
            return new ApiResponse<>("An Error occurred while fetching a subCategory by its Id", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> updateSubCategory(SubCategoryRequest subCategoryRequest, Long subCategoryId){
        try {
            Optional<SubCategory> subCategoryOptional = subCategoryRepo.findByIdAndDeletedFlag(subCategoryId, Constants.NO);
            if (subCategoryOptional.isPresent()) {
                SubCategory subCategory = subCategoryOptional.get();
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(subCategoryRequest.getDescription(),
                        subCategory::setDescription,
                        subCategory::getDescription);
                if (subCategoryRequest.getSubCategory() != null
                        && subCategoryRequest.getSubCategory().length() > 0
                        && !Objects.equals(subCategory.getSubCategory(),
                        subCategoryRequest.getSubCategory())) {
                    Optional<SubCategory> subCategoryOptional1 = subCategoryRepo
                            .findBySubCategoryAndDeletedFlag(subCategoryRequest.getSubCategory(), Constants.NO);

                    if (subCategoryOptional1.isPresent()) {
                        return new ApiResponse<>("SubCategory already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    subCategory.setSubCategory(subCategoryRequest.getSubCategory());

                }
                SubCategory updatedCategory = subCategoryRepo.save(subCategory);

                CategoryResponse subCategoryResponse = modelMapper.map(updatedCategory,
                        CategoryResponse.class);
                return new ApiResponse<>("Category updated successfully", subCategoryResponse, HttpStatus.OK.value());

            }else {
                return new ApiResponse<>("Category not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e){
            log.info("Updating Category Error: ", e);
            return new ApiResponse<>("Error updating a subCategory", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> deleteSubCategory(Long id){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<SubCategory> subCategoryOptional = subCategoryRepo.findByIdAndDeletedFlag(id, Constants.NO);

            if (subCategoryOptional.isPresent()) {
                SubCategory subCategory = subCategoryOptional.get();
                subCategory.setDeletedAt(LocalDateTime.now());
                subCategory.setDeletedBy(currentUser);
                subCategory.setDeletedFlag(Constants.YES);
                subCategoryRepo.save(subCategory);

                return new ApiResponse<>("SubCategory successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("SubCategory not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e){
            log.info("Deleting a subCategory error: ", e);
            return new ApiResponse<>("Error deleting a subCategory", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
