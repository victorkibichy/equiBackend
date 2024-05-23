package com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.ProductCategory;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.ProductCategoryRepository;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts.DTO.TypeOfProductRequest;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts.DTO.TypeOfProductResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import lombok.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TypeOfProductsService {
    private final TypeOfProductsRepository typeOfProductsRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> addTypeOfProduct(TypeOfProductRequest typeOfProductRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            if (typeOfProductRequest.getTypeOfProduct() == null) {
                return new ApiResponse<>("Type of Product is required.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
            if (typeOfProductsRepository.existsByTypeOfProduct(typeOfProductRequest.getTypeOfProduct())) {
                return new ApiResponse<>("Type of Product already exists.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
            if (typeOfProductRequest.getCategoryId() == null) {
                return new ApiResponse<>("Type of Product Category is required.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
            System.out.println(typeOfProductRequest.getUnits());
            if (typeOfProductRequest.getUnits() == null) {
                return new ApiResponse<>("Units of Measurements required", null, HttpStatus.BAD_REQUEST.value());
            }

            Optional<TypesOfProducts> typeOfProductsOptional = typeOfProductsRepository
                    .findByDeletedFlagAndTypeOfProduct(Constants.NO, typeOfProductRequest.getTypeOfProduct());

            if (typeOfProductsOptional.isEmpty()) {

                Optional<ProductCategory> productCategoryOptional = productCategoryRepository
                        .findByDeletedFlagAndId(Constants.NO, typeOfProductRequest.getCategoryId());

                if (productCategoryOptional.isEmpty()) {
                    return new ApiResponse<>("Product Category not found.", null,
                            HttpStatus.BAD_REQUEST.value());
                }

                ProductCategory category = productCategoryOptional.get();

                var typeOfProduct = TypesOfProducts.builder()
                        .typeOfProduct(typeOfProductRequest.getTypeOfProduct())
                        .description(typeOfProductRequest.getDescription())
                        .units(typeOfProductRequest.getUnits())
                        .category(category)
                        .build();

                TypesOfProducts addedTypeOfProduct = typeOfProductsRepository.save(typeOfProduct);

                TypeOfProductResponse typeOfProductResponse = modelMapper.map(addedTypeOfProduct,
                        TypeOfProductResponse.class);

                return new ApiResponse<TypeOfProductResponse>("Type of product was successfully added.",
                        typeOfProductResponse,
                        HttpStatus.CREATED.value());

            } else {
                return new ApiResponse<>("Type of Product already exists.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while adding Type of Product.", e);

            return new ApiResponse<>("An error occurred while adding Type of Product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> typeOfProductUpdate(TypeOfProductRequest typeOfProductRequest, Long id) {
        try {

            Optional<TypesOfProducts> typeOfProductOptional = typeOfProductsRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typeOfProductOptional.isPresent()) {
                TypesOfProducts typeProducts = typeOfProductOptional.get();
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(typeOfProductRequest.getDescription(),
                        typeProducts::setDescription,
                        typeProducts::getDescription);

                if (typeOfProductRequest.getCategoryId() != null) {
                    Optional<ProductCategory> productCategoryOptional = productCategoryRepository
                            .findByDeletedFlagAndId(Constants.NO, typeOfProductRequest.getCategoryId());

                    if (productCategoryOptional.isPresent()) {
                        ProductCategory category = productCategoryOptional.get();
                        typeProducts.setCategory(category);
                    } else {
                        return new ApiResponse<>("Category does not exist.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }

                }

                if (typeOfProductRequest.getTypeOfProduct() != null
                        && typeOfProductRequest.getTypeOfProduct().length() > 0
                        && !Objects.equals(typeProducts.getTypeOfProduct(),
                        typeOfProductRequest.getTypeOfProduct())) {
                    Optional<TypesOfProducts> typeOfProdsOptional = typeOfProductsRepository
                            .findByDeletedFlagAndTypeOfProduct(
                                    Constants.NO, typeOfProductRequest.getTypeOfProduct());

                    if (typeOfProdsOptional.isPresent()) {
                        return new ApiResponse<>("Type of product already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    typeProducts.setTypeOfProduct(typeOfProductRequest.getTypeOfProduct());
                }


                if (typeOfProductRequest.getUnits() == null) {
                    return new ApiResponse<>("Unit of measurement is required.", null, HttpStatus.BAD_REQUEST.value());
                }
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(typeOfProductRequest.getUnits(),
                        typeProducts::setUnits,
                        typeProducts::getUnits);

                TypesOfProducts updaTypesOfProducts = typeOfProductsRepository.save(typeProducts);

                TypeOfProductResponse typeOfProductResponse = modelMapper.map(updaTypesOfProducts,
                        TypeOfProductResponse.class);

                return new ApiResponse<TypeOfProductResponse>("Type of Product updated successfully.",
                        typeOfProductResponse, HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Type of Product not found.", null,
                        HttpStatus.BAD_REQUEST.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while updating the type of product.", e);
            return new ApiResponse<>("An error occurred while updating the type of product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAllTypeOfProducts(Long categoryId) {
        try {
            List<TypesOfProducts> typeOfProductsList = typeOfProductsRepository.findByDeletedFlag(Constants.NO);
            List<TypeOfProductResponse> typeOfProductsResponseList = typeOfProductsList.stream()
                    .filter(typesOfProducts -> (categoryId == null || typesOfProducts.getCategory().getId().equals(categoryId)))
                    .map(typeOfProduct -> modelMapper.map(typeOfProduct, TypeOfProductResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Type of Products fetched successfully.", typeOfProductsResponseList,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching Type of Products.", e);

            return new ApiResponse<>("An error occurred while fetching Type of Products.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getTypeOfProductById(Long id) {
        try {
            Optional<TypesOfProducts> typesOfProductsOptional = typeOfProductsRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typesOfProductsOptional.isPresent()) {
                TypesOfProducts typesOfProducts = typesOfProductsOptional.get();

                TypeOfProductResponse typeOfProductResponse = modelMapper.map(typesOfProducts,
                        TypeOfProductResponse.class);

                return new ApiResponse<>("Type of product fetched successfully.", typeOfProductResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Type of Product not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching Type of Product.", e);

            return new ApiResponse<>("An error occurred while fetching Type of Product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> typeOfProductDelete(Long id) {
        try {
            Optional<TypesOfProducts> typesOfProductsOptional = typeOfProductsRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typesOfProductsOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();

                TypesOfProducts typesOfProducts = typesOfProductsOptional.get();
                typesOfProducts.setDeletedAt(LocalDateTime.now());
                typesOfProducts.setDeletedBy(currentUser);
                typesOfProducts.setDeletedFlag(Constants.YES);

                TypesOfProducts deletedTypeOfProduct = typeOfProductsRepository.save(typesOfProducts);

                return new ApiResponse<>("Type of Product was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Type of Product not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting Type of Product.", e);

            return new ApiResponse<>("An error occurred while deleting Type of Product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
