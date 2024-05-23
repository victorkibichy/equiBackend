package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory.DTO.AgriBusinessProductCategoryRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory.DTO.AgriBusinessProductCategoryResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.DTO.ProductCategoryResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class AgriBusinessProductCategoryService {
    private final AgriBusinessProductCategoryRepository agriBusinessProductCategoryRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> createAgriBusinessProductCategory(AgriBusinessProductCategoryRequest agriBusinessProductCategoryRequest) {
        try {
            if (agriBusinessProductCategoryRequest.getProductCategory() == null) {
                return new ApiResponse<>("Agribusiness product category is required.", null,
                        HttpStatus.BAD_REQUEST.value());
            }

            Optional<AgriBusinessProductCategory> agriBusinessProductCategoryOptional = agriBusinessProductCategoryRepository
                    .findByDeletedFlagAndProductCategory(Constants.NO, agriBusinessProductCategoryRequest.getProductCategory());

            if (agriBusinessProductCategoryOptional.isEmpty()) {
                var createdAgriBusinessProductCategory = AgriBusinessProductCategory.builder()
                        .description(agriBusinessProductCategoryRequest.getDescription())
                        .productCategory(agriBusinessProductCategoryRequest.getProductCategory())
                        .build();

                    AgriBusinessProductCategory addedProductCategory = agriBusinessProductCategoryRepository.save(createdAgriBusinessProductCategory);

                ProductCategoryResponse productCategoryResponse = modelMapper.map(addedProductCategory,
                        ProductCategoryResponse.class);

                return new ApiResponse<>("Product category was successfully created.",
                        productCategoryResponse, HttpStatus.CREATED.value());

            } else {
                return new ApiResponse<>("Agribusiness product category already exists.", null,
                        HttpStatus.BAD_REQUEST.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while creating agribusiness product category.", e);

            return new ApiResponse<>("An error occurred while creating agribusiness product category.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    @Transactional
    public ApiResponse<?> getAllAgriBusinessProductCategories() {
        try {
            List<AgriBusinessProductCategory> agriBusinessProductCategoryList = agriBusinessProductCategoryRepository.findByDeletedFlag(Constants.NO);

            List<AgriBusinessProductCategoryResponse> agriBusinessProductCategoryResponses = agriBusinessProductCategoryList.stream()
                    .map(category -> modelMapper.map(category, AgriBusinessProductCategoryResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Agribusiness product categories fetched successfully.",
                    agriBusinessProductCategoryResponses, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching product categories.", e);

            return new ApiResponse<>("An error occurred while fetching agribusiness product categories.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> getAgriBusinessProductCategoryById(Long id) {
        try {
            Optional<AgriBusinessProductCategory> agriBusinessProductCategoryOptional = agriBusinessProductCategoryRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (agriBusinessProductCategoryOptional.isPresent()) {
                AgriBusinessProductCategory agriBusinessProductCategory= agriBusinessProductCategoryOptional.get();

                AgriBusinessProductCategoryResponse agriBusinessProductCategoryResponse = modelMapper.map(
                        agriBusinessProductCategory, AgriBusinessProductCategoryResponse.class);

                return new ApiResponse<>("Agribusiness product category was successfully fetched.", agriBusinessProductCategoryResponse,
                        HttpStatus.CREATED.value());

            } else {
                return new ApiResponse<>("Agribusiness product category not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching product category.", e);
            return new ApiResponse<>("An error occurred while fetching agribusiness product category.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> agriBusinessProductCategoryUpdate(AgriBusinessProductCategoryRequest agriBusinessProductCategoryRequest, Long id) {
        try {
            Optional<AgriBusinessProductCategory> agriBusinessProductCategoryOptional = agriBusinessProductCategoryRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (agriBusinessProductCategoryOptional.isPresent()) {
                AgriBusinessProductCategory agriBusinessProductCategory = agriBusinessProductCategoryOptional.get();

                if (agriBusinessProductCategoryRequest.getProductCategory() != null
                        && agriBusinessProductCategoryRequest.getProductCategory().length() > 0 &&
                        !Objects.equals(agriBusinessProductCategory.getProductCategory(),
                                agriBusinessProductCategoryRequest.getProductCategory())) {
                    Optional<AgriBusinessProductCategory> agriBusinessProductCategoryOptional1 = agriBusinessProductCategoryRepository
                            .findByDeletedFlagAndProductCategory(Constants.NO,
                                   agriBusinessProductCategoryRequest.getProductCategory());

                    if (agriBusinessProductCategoryOptional1.isPresent()) {
                        return new ApiResponse<>("Agribusiness product category already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }

                    agriBusinessProductCategory.setProductCategory(agriBusinessProductCategoryRequest.getProductCategory());
                }

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessProductCategory.getDescription(),
                        agriBusinessProductCategory::setDescription,
                        agriBusinessProductCategory::getDescription);

                AgriBusinessProductCategory updatedAgriBusinessProductCategory = agriBusinessProductCategoryRepository.save(agriBusinessProductCategory);

                AgriBusinessProductCategoryResponse agriBusinessProductCategoryResponse = modelMapper.map(updatedAgriBusinessProductCategory,
                        AgriBusinessProductCategoryResponse.class);

                return new ApiResponse<AgriBusinessProductCategoryResponse>("Agribusiness product category updated successfully.",
                        agriBusinessProductCategoryResponse, HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Agribusiness product category not found.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating the agribusiness product category.", e);
            return new ApiResponse<>("An error occurred while updating the agribusiness product category.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> agriBusinessProductCategoryDelete(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<AgriBusinessProductCategory> agriBusinessProductCategoryOptional = agriBusinessProductCategoryRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (agriBusinessProductCategoryOptional.isPresent()) {
                AgriBusinessProductCategory agriBusinessProductCategory = agriBusinessProductCategoryOptional.get();
                agriBusinessProductCategory.setDeletedAt(LocalDateTime.now());
                agriBusinessProductCategory.setDeletedBy(currentUser);
                agriBusinessProductCategory.setDeletedFlag(Constants.YES);

                agriBusinessProductCategoryRepository.save(agriBusinessProductCategory);

                return new ApiResponse<>("Agribusiness product category was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Agribusiness product category not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while deleting agribusiness product category.", e);

            return new ApiResponse<>("An error occurred while deleting agribusiness product category.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}



