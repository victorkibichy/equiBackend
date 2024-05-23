package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.TypeOfAgriBusinessProduct;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory.AgriBusinessProductCategory;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory.AgriBusinessProductCategoryRepository;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.TypeOfAgriBusinessProduct.DTO.TypeOfAgriBusinessProductRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.TypeOfAgriBusinessProduct.DTO.TypeOfAgriBusinessProductResponse;
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
public class TypeOfAgriBusinessProductService {
    private final TypeOfAgriBusinessProductRepository typeOfAgriBusinessProductRepository;
    private final AgriBusinessProductCategoryRepository agriBusinessProductCategoryRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> addTypeOfAgriBusinessProduct(TypeOfAgriBusinessProductRequest typeOfAgriBusinessProductRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            if (typeOfAgriBusinessProductRequest.getTypeOfAgriBusinessProduct() == null) {
                return new ApiResponse<>("Type of AgriBusiness Product is required.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
            if (typeOfAgriBusinessProductRepository.existsByTypeOfAgriBusinessProduct(typeOfAgriBusinessProductRequest.getTypeOfAgriBusinessProduct())) {
                return new ApiResponse<>("Type of Agribusiness Product already exists.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
            if (typeOfAgriBusinessProductRequest.getCategoryId() == null) {
                return new ApiResponse<>("Type of AgriBusiness Product Category is required.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
            System.out.println(typeOfAgriBusinessProductRequest.getUnits());
            if(typeOfAgriBusinessProductRequest.getUnits() == null){
                return new ApiResponse<>("Units of Measurements required", null, HttpStatus.BAD_REQUEST.value());
            }

            Optional<TypeOfAgriBusinessProduct> typeOfAgriBusinessProductOptional = typeOfAgriBusinessProductRepository
                    .findByDeletedFlagAndTypeOfAgriBusinessProduct(Constants.NO, typeOfAgriBusinessProductRequest.getTypeOfAgriBusinessProduct());

            if (typeOfAgriBusinessProductOptional.isEmpty()) {
                Optional<AgriBusinessProductCategory> agriBusinessProductCategoryOptional = agriBusinessProductCategoryRepository
                        .findByDeletedFlagAndId(Constants.NO, typeOfAgriBusinessProductRequest.getCategoryId());

                if (agriBusinessProductCategoryOptional.isEmpty()) {
                    return new ApiResponse<>("Agribusiness Product Category not found.", null,
                            HttpStatus.BAD_REQUEST.value());
                }

                AgriBusinessProductCategory category = agriBusinessProductCategoryOptional.get();

                var typeOfAgriBusinessProduct = TypeOfAgriBusinessProduct.builder()
                        .typeOfAgriBusinessProduct(typeOfAgriBusinessProductRequest.getTypeOfAgriBusinessProduct())
                        .description(typeOfAgriBusinessProductRequest.getDescription())
                        .units(typeOfAgriBusinessProductRequest.getUnits())
                        .category(category)
                        .build();

                TypeOfAgriBusinessProduct addedTypeOfAgriBusinessProduct = typeOfAgriBusinessProductRepository.save(typeOfAgriBusinessProduct);

                TypeOfAgriBusinessProductResponse typeOfAgriBusinessProductResponse = modelMapper.map(addedTypeOfAgriBusinessProduct,
                        TypeOfAgriBusinessProductResponse.class);

                return new ApiResponse<TypeOfAgriBusinessProductResponse>("Type of agribusiness product was successfully added.",
                        typeOfAgriBusinessProductResponse,
                        HttpStatus.CREATED.value());

            } else {
                return new ApiResponse<>("Type of Agribusiness Product already exists.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while adding Type of Agribusiness Product.", e);

            return new ApiResponse<>("An error occurred while adding Type of Agribusiness Product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> typeOfAgriBusinessProductUpdate(TypeOfAgriBusinessProductRequest typeOfAgriBusinessProductRequest, Long id) {
        try {

            Optional<TypeOfAgriBusinessProduct> typeOfAgriBusinessProductOptional = typeOfAgriBusinessProductRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typeOfAgriBusinessProductOptional.isPresent()) {
                TypeOfAgriBusinessProduct typeOfAgriBusinessProduct = typeOfAgriBusinessProductOptional.get();
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(typeOfAgriBusinessProductRequest.getDescription(),
                        typeOfAgriBusinessProduct::setDescription,
                        typeOfAgriBusinessProduct::getDescription);

                if (typeOfAgriBusinessProductRequest.getCategoryId() != null) {
                    Optional<AgriBusinessProductCategory> agriBusinessProductCategoryOptional = agriBusinessProductCategoryRepository
                            .findByDeletedFlagAndId(Constants.NO, typeOfAgriBusinessProductRequest.getCategoryId());

                    if (agriBusinessProductCategoryOptional.isPresent()) {
                        AgriBusinessProductCategory category = agriBusinessProductCategoryOptional.get();
                        typeOfAgriBusinessProduct.setCategory(category);
                    } else {
                        return new ApiResponse<>("Category does not exist.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }

                }

                if (typeOfAgriBusinessProductRequest.getTypeOfAgriBusinessProduct() != null
                        && typeOfAgriBusinessProductRequest.getTypeOfAgriBusinessProduct().length() > 0
                        && !Objects.equals(typeOfAgriBusinessProduct.getTypeOfAgriBusinessProduct(),
                        typeOfAgriBusinessProduct.getTypeOfAgriBusinessProduct())) {
                    Optional<TypeOfAgriBusinessProduct> typeOfAgriBusinessProductOptional1 = typeOfAgriBusinessProductRepository
                            .findByDeletedFlagAndTypeOfAgriBusinessProduct(
                                    Constants.NO, typeOfAgriBusinessProductRequest.getTypeOfAgriBusinessProduct());

                    if (typeOfAgriBusinessProductOptional1.isPresent()) {
                        return new ApiResponse<>("Type of agribusiness product already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    typeOfAgriBusinessProduct.setTypeOfAgriBusinessProduct(typeOfAgriBusinessProductRequest.getTypeOfAgriBusinessProduct());
                }


                if (typeOfAgriBusinessProductRequest.getUnits() == null) {
                    return new ApiResponse<>("Unit of measurement is required.", null, HttpStatus.BAD_REQUEST.value());
                }
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(typeOfAgriBusinessProductRequest.getUnits(),
                        typeOfAgriBusinessProduct::setUnits,
                        typeOfAgriBusinessProduct::getUnits);

                TypeOfAgriBusinessProduct updateTypesOfAgriBusinessProducts = typeOfAgriBusinessProductRepository.save(typeOfAgriBusinessProduct);

                TypeOfAgriBusinessProductResponse typeOfAgriBusinessProductResponse = modelMapper.map(updateTypesOfAgriBusinessProducts,
                        TypeOfAgriBusinessProductResponse.class);

                return new ApiResponse<TypeOfAgriBusinessProductResponse>("Type of agribusiness product updated successfully.",
                        typeOfAgriBusinessProductResponse, HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Type of agribusiness product not found.", null,
                        HttpStatus.BAD_REQUEST.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while updating the type of agribusiness product.", e);
            return new ApiResponse<>("An error occurred while updating the type of agribusiness product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getTypeOfAgriBusinessProductById(Long id) {
        try {
            Optional<TypeOfAgriBusinessProduct> typeOfAgriBusinessProductOptional = typeOfAgriBusinessProductRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typeOfAgriBusinessProductOptional.isPresent()) {
                TypeOfAgriBusinessProduct typeOfAgriBusinessProduct = typeOfAgriBusinessProductOptional.get();

                TypeOfAgriBusinessProductResponse typeOfAgriBusinessProductResponse = modelMapper.map(typeOfAgriBusinessProduct,
                        TypeOfAgriBusinessProductResponse.class);

                return new ApiResponse<>("Type of agribusiness product fetched successfully.", typeOfAgriBusinessProductResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Type of Agribusiness Product not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching Type of Agribusiness Product.", e);

            return new ApiResponse<>("An error occurred while fetching Type of agibusiness product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAllTypeOfAgriBusinessProducts() {
        try {
            List<TypeOfAgriBusinessProduct> typeOfAgriBusinessProductList = typeOfAgriBusinessProductRepository.findByDeletedFlag(Constants.NO);
            List<TypeOfAgriBusinessProductResponse> typeOfAgriBusinessProductResponseList = typeOfAgriBusinessProductList.stream()
                    .map(typeOfAgriBusinessProduct -> modelMapper.map(typeOfAgriBusinessProduct, TypeOfAgriBusinessProductResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Type of Agribusiness Products fetched successfully.", typeOfAgriBusinessProductResponseList,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching Type of Agribusiness Products.", e);

            return new ApiResponse<>("An error occurred while fetching Type of Agribusiness Products.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> typeOfAgriBusinessProductDelete(Long id) {
        try {
            Optional<TypeOfAgriBusinessProduct> typesOfAgriBusinessProductsOptional = typeOfAgriBusinessProductRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typesOfAgriBusinessProductsOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();

                TypeOfAgriBusinessProduct typeOfAgriBusinessProduct = typesOfAgriBusinessProductsOptional.get();
                typeOfAgriBusinessProduct.setDeletedAt(LocalDateTime.now());
                typeOfAgriBusinessProduct.setDeletedBy(currentUser);
                typeOfAgriBusinessProduct.setDeletedFlag(Constants.YES);

                TypeOfAgriBusinessProduct deletedTypeOfAgriBusinessProduct = typeOfAgriBusinessProductRepository.save(typeOfAgriBusinessProduct);

                return new ApiResponse<>("Type of Agribusiness Product was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Type of Agribusiness Product not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting Type of Agribusiness Product.", e);

            return new ApiResponse<>("An error occurred while deleting Type of Agribusiness Product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
