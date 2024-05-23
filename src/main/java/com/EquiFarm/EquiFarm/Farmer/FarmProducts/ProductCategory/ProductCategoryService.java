package com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory;

import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.DTO.ProductCategoryRequest;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.DTO.ProductCategoryResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> createProductCategory(ProductCategoryRequest productCategoryRequest) {
        try {
            if (productCategoryRequest.getProductCategory() == null) {
                return new ApiResponse<>("Product category is required.", null,
                        HttpStatus.BAD_REQUEST.value());
            }

            Optional<ProductCategory> productCategoryOptional = productCategoryRepository
                    .findByDeletedFlagAndProductCategory(Constants.NO, productCategoryRequest.getProductCategory());

            if (productCategoryOptional.isEmpty()) {
                var createdProductCategory = ProductCategory.builder()
                        .description(productCategoryRequest.getDescription())
                        .productCategory(productCategoryRequest.getProductCategory())
                        .build();

                ProductCategory addedProductCategory = productCategoryRepository.save(createdProductCategory);

                ProductCategoryResponse productCategoryResponse = modelMapper.map(addedProductCategory,
                        ProductCategoryResponse.class);

                return new ApiResponse<>("Product category was successfully created.",
                        productCategoryResponse, HttpStatus.CREATED.value());

            } else {
                return new ApiResponse<>("Product category already exists.", null,
                        HttpStatus.BAD_REQUEST.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while creating product category.", e);

            return new ApiResponse<>("An error occurred while creating product category.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    @Transactional
    public ApiResponse<?> productCategoryUpdate(ProductCategoryRequest productCategoryRequest, Long id) {
        try {
            Optional<ProductCategory> productCategoryOptional = productCategoryRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (productCategoryOptional.isPresent()) {
                ProductCategory productCategory = productCategoryOptional.get();

                if (productCategoryRequest.getProductCategory() != null
                        && productCategoryRequest.getProductCategory().length() > 0 &&
                        !Objects.equals(productCategory.getProductCategory(),
                                productCategoryRequest.getProductCategory())) {
                    Optional<ProductCategory> productCatOptional = productCategoryRepository
                            .findByDeletedFlagAndProductCategory(Constants.NO,
                                    productCategoryRequest.getProductCategory());

                    if (productCatOptional.isPresent()) {
                        return new ApiResponse<>("Product category already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }

                    productCategory.setProductCategory(productCategoryRequest.getProductCategory());
                }

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(productCategoryRequest.getDescription(),
                        productCategory::setDescription,
                        productCategory::getDescription);

                ProductCategory updatedCategory = productCategoryRepository.save(productCategory);

                ProductCategoryResponse productCategoryResponse = modelMapper.map(updatedCategory,
                        ProductCategoryResponse.class);

                return new ApiResponse<ProductCategoryResponse>("Product Category updated successfully.",
                        productCategoryResponse, HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Product category not found.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating the type of product.", e);
            return new ApiResponse<>("An error occurred while updating the type of product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAllProductCategories() {
        try {
            List<ProductCategory> productCategoriesList = productCategoryRepository.findByDeletedFlag(Constants.NO);

            List<ProductCategoryResponse> productCategoryResponses = productCategoriesList.stream()
                    .map(category -> modelMapper.map(category, ProductCategoryResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Product categories fetched successfully.",
                    productCategoryResponses, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching product categories.", e);

            return new ApiResponse<>("An error occurred while fetching product categories.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getTypeOfCategoryById(Long id) {
        try {
            Optional<ProductCategory> productCategoryOptional = productCategoryRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (productCategoryOptional.isPresent()) {
                ProductCategory productCategory = productCategoryOptional.get();

                ProductCategoryResponse productCategoryResponse = modelMapper.map(
                        productCategory, ProductCategoryResponse.class);

                return new ApiResponse<>("Product category was successfully fetched.", productCategoryResponse,
                        HttpStatus.CREATED.value());

            } else {
                return new ApiResponse<>("Product category not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching product category.", e);
            return new ApiResponse<>("An error occurred while fetching product category.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> productCategoryDelete(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<ProductCategory> productCategoryOptional = productCategoryRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (productCategoryOptional.isPresent()) {
                ProductCategory productCategory = productCategoryOptional.get();
                productCategory.setDeletedAt(LocalDateTime.now());
                productCategory.setDeletedBy(currentUser);
                productCategory.setDeletedFlag(Constants.YES);

                productCategoryRepository.save(productCategory);

                return new ApiResponse<>("Product category was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Product category not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while deleting Product category.", e);

            return new ApiResponse<>("An error occurred while deleting Product category.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
