package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusiness;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProdDisplayImage.AgriBusinessDisplayImageRepository;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProdDisplayImage.AgriBusinessDisplayImages;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProdDisplayImage.DTO.AgriBusinessDisplayImagesRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.DTO.AgriBusinessProductDispImageRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.DTO.AgriBusinessProductRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.DTO.AgriBusinessProductResponse;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.Inventory.AgriInventoryService;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.TypeOfAgriBusinessProduct.TypeOfAgriBusinessProduct;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.TypeOfAgriBusinessProduct.TypeOfAgriBusinessProductRepository;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessRepository;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.SellingPoint;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.ValueChain.ValueChainRepo;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
public class AgriBusinessProductService {
    private final AgriBusinessProductRepository agriBusinessProductRepository;
    private final TypeOfAgriBusinessProductRepository typeOfAgriBusinessProductsRepository;

    private final AgriBusinessDisplayImageRepository agriBusinessDisplayImageRepository;
    private final ModelMapper modelMapper;
    private final AgriBusinessRepository agriBusinessRepository;
    private final AgriInventoryService agriInventoryService;
    private final ValueChainRepo valueChainRepo;

    @Transactional
    public ApiResponse<?> getAllAgriBusinessProducts(Long categoryId,
                                             Long productTypeId,
                                             Long agriBusinessId,
                                             Boolean verified,
                                             Boolean onStock,
                                             SellingPoint sellingPoint) {
        try {
            List<AgriBusinessProduct> agriBusinessProductList = agriBusinessProductRepository.findByDeletedFlag(Constants.NO);

            List<AgriBusinessProductResponse> agriBusinessProductResponseList = agriBusinessProductList
                    .stream()
                    .filter(product->(categoryId == null || product.getAgriBusinessProductCategory().getId().equals(categoryId)))
                    .filter(product-> (productTypeId == null || product.getTypeOfAgriBuisnessProduct().getId().equals(productTypeId)))
                    .filter(product-> (agriBusinessId == null || product.getAgriBusiness().getId().equals(agriBusinessId)))
                    .filter(product-> (verified == null || product.getIsVerified().equals(verified)))
                    .filter(product-> (onStock == null || product.getOnStock().equals(onStock)))
//                    .filter(product-> (sellingPoint == null || product.getSellingPoint().equals(sellingPoint)))
                    .map(product -> modelMapper.map(product, AgriBusinessProductResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Agribusiness Products fetched successfully.", agriBusinessProductResponseList,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching AgriBusiness Products.", e);

            return new ApiResponse<>("An error occurred while fetching agribusiness Products.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> createAgriBusinessProduct(AgriBusinessProductRequest agriBusinessProductsRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<AgriBusiness> agriBusinessOptional = agriBusinessRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());
            if (agriBusinessOptional.isEmpty()) {
                return new ApiResponse<>("Agribusiness not found.", null, HttpStatus.NOT_FOUND.value());
            }
            AgriBusiness currentAgribusiness = agriBusinessOptional.get();
            // Validate the request
            if (agriBusinessProductsRequest.getAgriBusinessProductImage() == null) {
                return new ApiResponse<>("Image Product is required.", null, HttpStatus.BAD_REQUEST.value());
            }
            if (agriBusinessProductsRequest.getTypeOfAgriBusinessProductId() == null) {
                return new ApiResponse<>("Type of agribusiness product is required.", null, HttpStatus.BAD_REQUEST.value());
            }
            if (agriBusinessProductsRequest.getUnitsAvailable() == null) {
                return new ApiResponse<>("Units available is required.", null, HttpStatus.BAD_REQUEST.value());
            }
            if (agriBusinessProductsRequest.getUnitsAvailable() <= 0){
                return new ApiResponse<>("Units available must be greater than 0.", null, HttpStatus.BAD_REQUEST.value());
            }
            if (agriBusinessProductsRequest.getPricePerUnit() == null) {
                return new ApiResponse<>("Price per unit is required.", null, HttpStatus.BAD_REQUEST.value());
            }

            Optional<TypeOfAgriBusinessProduct> typeOfAgriBusinessProductOptional = typeOfAgriBusinessProductsRepository.findByDeletedFlagAndId(
                    Constants.NO,
                    agriBusinessProductsRequest.getTypeOfAgriBusinessProductId());

            if (typeOfAgriBusinessProductOptional.isEmpty()) {
                return new ApiResponse<>("Type of Agribusiness Product not found.", null, HttpStatus.NOT_FOUND.value());
            }

            TypeOfAgriBusinessProduct typeOfAgriBusinessProduct = typeOfAgriBusinessProductOptional.get();

            // Create the agribusiness product
            AgriBusinessProduct agriBusinessProduct = AgriBusinessProduct.builder()
                    .agriBusinessProductCategory(typeOfAgriBusinessProduct.getCategory())
                    .agriBusinessProductImage(agriBusinessProductsRequest.getAgriBusinessProductImage())
                    .typeOfAgriBuisnessProduct(typeOfAgriBusinessProduct)
                    .latitude(agriBusinessProductsRequest.getLatitude())
                    .longitude(agriBusinessProductsRequest.getLongitude())
                    .unitOfMeasurements(typeOfAgriBusinessProduct.getUnits())
                    .unitsAvailable(agriBusinessProductsRequest.getUnitsAvailable())
                    .pricePerUnit(agriBusinessProductsRequest.getPricePerUnit())
                    .agriBusiness(currentAgribusiness)
                    .agriBusiness_Product_Description(agriBusinessProductsRequest.getDescription())

//                    Value chain
//                    .valueChains(agriBusinessProductsRequest.)

                    .sellingPoint(SellingPoint.MARKETPLACE)
                    .onStock(true)
                    .build();

//            if (agriBusinessProductsRequest.getValueChainIds() != null) {
//                if (agriBusinessProductsRequest.getValueChainIds().size() == 0) {
//                    return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
//                }
//
//                List<ValueChain> valueChainList = new ArrayList<>();
//                for (Long valueId : agriBusinessProductsRequest.getValueChainIds()) {
//                    Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
//                    if (valueChainOptional.isEmpty()) {
//                        return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
//                    }
//                    ValueChain valueChain = valueChainOptional.get();
//                    valueChainList.add(valueChain);
//                    agriBusinessProduct.setValueChains(valueChainList);
//
//                    valueChainRepo.save(valueChain);
//                }
////                agriBusinessProduct.setAgriBusinessValueChains(agriBusinessValueChains);
//            }


//            if (agriBusinessProductsRequest.getValueChainIds() != null) {
//                if (agriBusinessProductsRequest.getValueChainIds().size() == 0) {
//                    return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
//                }
//
//                for (Long valueId : agriBusinessProductsRequest.getValueChainIds()) {
//                    Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
//                    if (valueChainOptional.isEmpty()) {
//                        return new ApiResponse<>("Value chain with id " + valueId + " does not exist",
//                                null, HttpStatus.NOT_FOUND.value());
//                    }
//                    ValueChain valueChain = valueChainOptional.get();
//                    if (!agriBusinessProduct.getValueChains().contains(valueChain)) {
//                        agriBusinessProduct.getValueChains().add(valueChain);
//                        valueChain.getAgriBusinessProductList().add(agriBusinessProduct);
//                        valueChainRepo.save(valueChain);
//                    }
//                }
//            }

            if (agriBusinessProductsRequest.getValueChainIds() != null) {
                if (agriBusinessProductsRequest.getValueChainIds().size() == 0) {
                    return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                }

                List<ValueChain> valueChainList = new ArrayList<>();
                for (Long valueId : agriBusinessProductsRequest.getValueChainIds()) {
                    Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                    if (valueChainOptional.isEmpty()) {
                        return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                    }
                    ValueChain valueChain = valueChainOptional.get();
                    valueChainList.add(valueChain);
                    agriBusinessProduct.setValueChains(valueChainList);
                    valueChain.getAgriBusinessProductsList().add(agriBusinessProduct);
                    valueChainRepo.save(valueChain);
                }
            }

            agriInventoryService.createAgriInventory(agriBusinessProduct);

            AgriBusinessProduct createdAgriBusinessProduct = agriBusinessProductRepository.save(agriBusinessProduct);
            AgriBusinessProductResponse agriBusinessProductResponse = modelMapper.map(createdAgriBusinessProduct, AgriBusinessProductResponse.class);

            return new ApiResponse<>("Agribusiness Product was successfully created.", agriBusinessProductResponse,
                    HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error("An error occurred while creating an Agribusiness product.", e);
            return new ApiResponse<>("An error occurred while creating an Agribusiness product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAgriBusinessProductByAgriBusinessId(Long agriBusinessId) {
        try {
            Optional<AgriBusiness> agriBusinessOptional = agriBusinessRepository.findByDeletedFlagAndId(Constants.NO, agriBusinessId);

            if (agriBusinessOptional.isPresent()) {
                AgriBusiness agriBusiness = agriBusinessOptional.get();

                List<AgriBusinessProduct> agriBusinessProductList = agriBusinessProductRepository.findByDeletedFlag(Constants.NO);

                List<AgriBusinessProduct> agriBusinessProducts = agriBusinessProductList.stream()
                        .filter(product -> product.getAgriBusiness() == agriBusiness)
                        .collect(Collectors.toList());

                List<AgriBusinessProductResponse> agriBusinessProductResponseList = agriBusinessProducts.stream()
                        .map(agriBusinessProduct -> modelMapper.map(agriBusinessProduct, AgriBusinessProductResponse.class))
                        .collect(Collectors.toList());

                return new ApiResponse<>("Agribusiness' agribusiness products fetched successfully.", agriBusinessProductResponseList,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Agribusiness not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching Agribusiness' agribusiness products.", e);
            return new ApiResponse<>("An error occurred while fetching Agribusiness' agribusiness products.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> getAgriBusinessProductById(Long id) {
        try {
            Optional<AgriBusinessProduct> agriBusinessProductOptional = agriBusinessProductRepository.findByDeletedFlagAndId(Constants.NO,
                    id);

            if (agriBusinessProductOptional.isPresent()) {
                AgriBusinessProduct agriBusinessProduct = agriBusinessProductOptional.get();

                AgriBusinessProductResponse agriBusinessProductResponse = modelMapper.map(agriBusinessProduct, AgriBusinessProductResponse.class);

                return new ApiResponse<>("Agribusiness Product fetched successfully.", agriBusinessProductResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Type of Product not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching Agribusiness Products.", e);

            return new ApiResponse<>("An error occurred while fetching Agribusiness Product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    private boolean hasPermissionToUpdateProduct(AgriBusinessProduct agriBusinessProduct, User currentUser) {
        return agriBusinessProduct.getAgriBusiness().getUser().getId().equals(currentUser.getId());
    }
    @Transactional
    public ApiResponse<?> updateAgriBusinessProducts(AgriBusinessProductRequest agriBusinessProductRequest, Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<AgriBusinessProduct> agriBusinessProductOptional = agriBusinessProductRepository.findByDeletedFlagAndId(Constants.NO,
                    id);

            if (agriBusinessProductOptional.isPresent()) {
                AgriBusinessProduct agriBusinessProduct = agriBusinessProductOptional.get();

                if (hasPermissionToUpdateProduct(agriBusinessProduct, currentUser)) {
                    updateProductFields(agriBusinessProduct, agriBusinessProductRequest);

                    if (agriBusinessProductRequest.getTypeOfAgriBusinessProductId() != null) {
                        Optional<TypeOfAgriBusinessProduct> typeOfAgriBusinessProductOptional = typeOfAgriBusinessProductsRepository
                                .findByDeletedFlagAndId(Constants.NO, agriBusinessProductRequest.getTypeOfAgriBusinessProductId());

                        if (typeOfAgriBusinessProductOptional.isPresent()) {
                            TypeOfAgriBusinessProduct typesOfAgriBusinessProduct = typeOfAgriBusinessProductOptional.get();
                            agriBusinessProduct.setTypeOfAgriBuisnessProduct(typesOfAgriBusinessProduct);
                            agriBusinessProduct.setAgriBusinessProductCategory(typesOfAgriBusinessProduct.getCategory());
                        } else {
                            return new ApiResponse<>("Agribusiness product not found.", null,
                                    HttpStatus.BAD_REQUEST.value());
                        }
                    }


                    if (agriBusinessProductRequest.getAgriBusinessDisplayImages() != null
                            && !agriBusinessProductRequest.getAgriBusinessDisplayImages().isEmpty()) {
                        updateAgriBusinessDisplayImages(agriBusinessProduct, agriBusinessProductRequest.getAgriBusinessDisplayImages());
                    }

                    AgriBusinessProduct updateAgriBusinessProduct = agriBusinessProductRepository.save(agriBusinessProduct);

                    AgriBusinessProductResponse agriBusinessProductResponse = modelMapper.map(updateAgriBusinessProduct,
                            AgriBusinessProductResponse.class);

                    return new ApiResponse<>("Agribusiness product was successfully updated.", agriBusinessProductResponse,
                            HttpStatus.OK.value());
                } else {
                    return new ApiResponse<>("You are not allowed to perform this action.", null,
                            HttpStatus.BAD_REQUEST.value());
                }
            } else {
                return new ApiResponse<>("Agribusiness product not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating the agribusiness product.", e);
            return new ApiResponse<>("An error occurred while updating the agribusiness product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    private void updateProductFields(AgriBusinessProduct agriBusinessProduct, AgriBusinessProductRequest agriBusinessProductRequest) {
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessProductRequest.getOnStock(),
                agriBusinessProduct::setOnStock, agriBusinessProduct::getOnStock);
        // FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmProductsRequest.getProductName(),
        // farmProduct::setProductName, farmProduct::getProductName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessProductRequest.getAgriBusinessProductImage(),
                agriBusinessProduct::setAgriBusinessProductImage, agriBusinessProduct::getAgriBusinessProductImage);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessProductRequest.getLongitude(),
                agriBusinessProduct::setLongitude, agriBusinessProduct::getLongitude);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessProductRequest.getLatitude(),
                agriBusinessProduct::setLatitude, agriBusinessProduct::getLatitude);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessProductRequest.getDescription(),
                agriBusinessProduct::setAgriBusiness_Product_Description, agriBusinessProduct::getAgriBusiness_Product_Description);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessProductRequest.getUnitsAvailable(),
                agriBusinessProduct::setUnitsAvailable, agriBusinessProduct::getUnitsAvailable);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessProductRequest.getPricePerUnit(),
                agriBusinessProduct::setPricePerUnit, agriBusinessProduct::getPricePerUnit);
    }
    private void updateAgriBusinessDisplayImages(AgriBusinessProduct agriBusinessProduct, List<AgriBusinessDisplayImagesRequest> agriBusinessDisplayImagesRequestList) {
        Set<String> existingImageSet = agriBusinessProduct.getAgriBusinessDisplayImages().stream()
                .map(AgriBusinessDisplayImages::getAgriBusinessDisplayImage)
                .collect(Collectors.toSet());

        List<AgriBusinessDisplayImages> savedAgriBusinessDisplayImages = agriBusinessDisplayImagesRequestList.stream()
                .map(agriBusinessDisplayImages -> {
                    String image = agriBusinessDisplayImages.getAgriBusinessDisplayImages();
                    if (!existingImageSet.contains(image)) {
                        AgriBusinessDisplayImages agriBusinessDisplayImages1 = new AgriBusinessDisplayImages();
                        agriBusinessDisplayImages.setAgriBusinessDisplayImages(image);
                        return agriBusinessDisplayImageRepository.save(agriBusinessDisplayImages1);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        agriBusinessProduct.getAgriBusinessDisplayImages().addAll(savedAgriBusinessDisplayImages);
    }
    @Transactional
    public ApiResponse<?> removeAgriBusinessDisplayImages(AgriBusinessProductDispImageRequest agriBusinessProductDispImageRequest, Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<AgriBusinessProduct> agriBusinessProductOptional = agriBusinessProductRepository.findByDeletedFlagAndId(Constants.NO,
                    id);
            if (agriBusinessProductOptional.isPresent()) {
                AgriBusinessProduct agriBusinessProduct = agriBusinessProductOptional.get();

                if (hasPermissionToUpdateProduct(agriBusinessProduct, currentUser)) {
                    if (agriBusinessProductDispImageRequest.getAgriBusinessDisplayImagesIds() != null
                            && !agriBusinessProductDispImageRequest.getAgriBusinessDisplayImagesIds().isEmpty()) {
                        List<AgriBusinessDisplayImages> agriBusinessDisplayImages = agriBusinessDisplayImageRepository
                                .findAllById(agriBusinessProductDispImageRequest.getAgriBusinessDisplayImagesIds());

                        if (!agriBusinessDisplayImages.isEmpty()) {
                            List<AgriBusinessDisplayImages> filteredImages = agriBusinessDisplayImages.stream()
                                    .filter(images -> images.getDeletedFlag() == Constants.NO)
                                    .collect(Collectors.toList());

                            agriBusinessProduct.getAgriBusinessDisplayImages().removeAll(filteredImages);
                        }
                    }

                    AgriBusinessProduct updatedAgriBusinessProduct = agriBusinessProductRepository.save(agriBusinessProduct);

                    AgriBusinessProductResponse agriBusinessProductsResponse = modelMapper.map(updatedAgriBusinessProduct,
                            AgriBusinessProductResponse.class);

                    return new ApiResponse<>("Agribusiness Display image was successfully removed.", agriBusinessProductsResponse,
                            HttpStatus.OK.value());
                } else {
                    return new ApiResponse<>("You are not allowed to perform this action.", null,
                            HttpStatus.BAD_REQUEST.value());
                }

            } else {
                return new ApiResponse<>("Agribusiness Product not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while removing display image.", e);
            return new ApiResponse<>("An error occurred while removing display image.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> agriBusinessProductDelete(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<AgriBusinessProduct> agriBusinessProductOptional = agriBusinessProductRepository.findByDeletedFlagAndId(Constants.NO,
                    id);
            if (agriBusinessProductOptional.isPresent()) {
                AgriBusinessProduct agriBusinessProduct = agriBusinessProductOptional.get();

                agriBusinessProduct.setDeletedAt(LocalDateTime.now());
                agriBusinessProduct.setDeletedBy(currentUser);
                agriBusinessProduct.setDeletedFlag(Constants.YES);

                AgriBusinessProduct deletedAgriBusinessProduct = agriBusinessProductRepository.save(agriBusinessProduct);

                return new ApiResponse<>("Agribusiness product was successfully deleted.", null, HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Agribusiness product not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting agribusiness product.", e);
            return new ApiResponse<>("An error occurred while deleting agribusiness product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}





