package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product;


import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Inventory.InventoryService;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.FarmProductDispImageRequest;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.FarmProductsRequest;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.FarmProductsResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DTO.DisplayImagesRequest;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DisplayImages;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DisplayImagesRepository;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.ProductCategoryRepository;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts.TypeOfProductsRepository;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts.TypesOfProducts;
import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Farmer.FarmerRepository;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.ValueChain.ValueChainRepo;
import com.EquiFarm.EquiFarm.Warehouse.Warehouse;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.DTO.WHProductRequest;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.DTO.WHProductResponse;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.WHProductStatus;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.WarehouseProduct;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.WarehouseProductRepo;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseRepo;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FarmProductService {
    private final FarmProductsRepository farmProductsRepository;
    private final TypeOfProductsRepository typeOfProductsRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final DisplayImagesRepository displayImagesRepository;
    private final ModelMapper modelMapper;
    private final FarmerRepository farmerRepository;
    private final WarehouseProductRepo warehouseProductRepo;
    private final WarehouseRepo warehouseRepo;
    private final InventoryService inventoryService;
    private final ValueChainRepo valueChainRepo;
    private Double discount;

    public ApiResponse<?> createFarmProduct(FarmProductsRequest farmProductsRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (farmerOptional.isEmpty()) {
                return new ApiResponse<>("Farmer not found.", null, HttpStatus.NOT_FOUND.value());
            }

            Farmer currentFarmer = farmerOptional.get();

            // Validate the request
            if (farmProductsRequest.getProductImage() == null) {
                return new ApiResponse<>("Image Product is required.", null, HttpStatus.BAD_REQUEST.value());
            }
            if (farmProductsRequest.getTypeOfProductId() == null) {
                return new ApiResponse<>("Type of product is required.", null, HttpStatus.BAD_REQUEST.value());
            }
            if (farmProductsRequest.getUnitsAvailable() == null) {
                return new ApiResponse<>("Units available is required.", null, HttpStatus.BAD_REQUEST.value());
            }
            if (farmProductsRequest.getUnitsAvailable() <= 1){
                return new ApiResponse<>("Units available must be greater than 1.", null, HttpStatus.BAD_REQUEST.value());
            }
            if (farmProductsRequest.getPricePerUnit() == null) {
                return new ApiResponse<>("Price per unit is required.", null, HttpStatus.BAD_REQUEST.value());
            }


            Optional<TypesOfProducts> typeOfProductOptional = typeOfProductsRepository.findByDeletedFlagAndId(
                    Constants.NO,
                    farmProductsRequest.getTypeOfProductId());

            if (typeOfProductOptional.isEmpty()) {
                return new ApiResponse<>("Type of Product not found.", null, HttpStatus.NOT_FOUND.value());
            }

            TypesOfProducts typeOfProduct = typeOfProductOptional.get();

            // Create the farm product
            FarmProducts farmProduct = FarmProducts.builder()
                    .category(typeOfProduct.getCategory())
                    .productImage(farmProductsRequest.getProductImage())
                    .typeOfProduct(typeOfProduct)
                    .latitude(farmProductsRequest.getLatitude())
                    .longitude(farmProductsRequest.getLongitude())
                    .unitOfMeasurements(typeOfProduct.getUnits())
                    .unitsAvailable(farmProductsRequest.getUnitsAvailable())
                    .pricePerUnit(farmProductsRequest.getPricePerUnit())
                    .farmer(currentFarmer)
                    .product_Description(farmProductsRequest.getDescription())
                    .sellingPoint(SellingPoint.MARKETPLACE)
                    .onStock(true)

                    .availabilityDate(farmProductsRequest.getAvailabilityDate()) // Set availability date
                    .isPreListed(farmProductsRequest.getIsPreListed()) // Set isPreListed

                    .discount(farmProductsRequest.getDiscount()) // Set discount
                    .expirationDate(farmProductsRequest.getExpirationDate()) // Set expiration date

                    .build();
            if (farmProductsRequest.getDisplayImages() != null) {
                List<DisplayImagesRequest> displayImagesRequestList = farmProductsRequest.getDisplayImages();
                List<DisplayImages> savedDisplayImages = displayImagesRequestList.stream()
                        .map(displayImages -> {
                            DisplayImages displayImage = new DisplayImages();
                            displayImage.setDisplayImage(displayImages.getDisplayImages());
                            return displayImagesRepository.save(displayImage);
                        })
                        .collect(Collectors.toList());

                farmProduct.setDisplayImages(savedDisplayImages);
            }
            if (farmProductsRequest.getValueChainIds() != null) {
                if (farmProductsRequest.getValueChainIds().size() == 0) {
                    return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                }

                List<ValueChain> valueChainList = new ArrayList<>();
                for (Long valueId : farmProductsRequest.getValueChainIds()) {
                    Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                    if (valueChainOptional.isEmpty()) {
                        return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                    }
                    ValueChain valueChain = valueChainOptional.get();
                       valueChainList.add(valueChain);
                        farmProduct.setValueChains(valueChainList);
                        valueChain.getFarmProductsList().add(farmProduct);
                        valueChainRepo.save(valueChain);
                }
            }

            inventoryService.createInventory(farmProduct);
            FarmProducts createdFarmProduct = farmProductsRepository.save(farmProduct);
            FarmProductsResponse farmProductResponse = modelMapper.map(createdFarmProduct, FarmProductsResponse.class);

            return new ApiResponse<>("Farm product was successfully created.", farmProductResponse,
                    HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error("An error occurred while creating a Farm product.", e);
            return new ApiResponse<>("An error occurred while creating a Farm product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAllFarmProducts(Long categoryId,
                                             Long productTypeId,
                                             Long farmerId,
                                             Boolean verified,
                                             Boolean onStock,
                                             Boolean preListed
//                                           SellingPoint sellingPoint)
                                          )
     {
        try {
            List<FarmProducts> farmProductsList = farmProductsRepository.findByDeletedFlag(Constants.NO);

            List<FarmProductsResponse> farmProductResponseList = farmProductsList
                    .stream()
                    .filter(product->(categoryId == null || product.getCategory().getId().equals(categoryId)))
                    .filter(product-> (productTypeId == null || product.getTypeOfProduct().getId().equals(productTypeId)))
//                    .filter(product-> (farmerId == null || product.getFarmer().getId().equals(farmerId)))
//                    .filter(product-> (verified == null || product.getIsVerified().equals(verified)))
                    .filter(product-> (onStock == null || product.getOnStock().equals(onStock)))

                    .filter(product-> (preListed == null || product.getIsPreListed().equals(preListed)))

//                    .filter(product-> (sellingPoint == null || product.getSellingPoint().equals(sellingPoint)))

                    .map(product -> modelMapper.map(product, FarmProductsResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Farm Products fetched successfully.", farmProductResponseList,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching Farm Products.", e);

            return new ApiResponse<>("An error occurred while fetching Farm Products.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    public Page<FarmProducts> getPaginatedProducts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return farmProductsRepository.findAll(pageRequest);
    }
    public ApiResponse<?> getFarmProductsById(Long id) {
        try {
            Optional<FarmProducts> farmProductOptional = farmProductsRepository.findByDeletedFlagAndId(Constants.NO,
                    id);

            if (farmProductOptional.isPresent()) {
                FarmProducts farmProduct = farmProductOptional.get();

                FarmProductsResponse farmProductResponse = modelMapper.map(farmProduct, FarmProductsResponse.class);

                return new ApiResponse<>("Farm Product fetched successfully.", farmProductResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Type of Product not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching Farm Products.", e);

            return new ApiResponse<>("An error occurred while fetching Farm Product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getFarmProductByFarmerId(Long farmerId) {
        try {
            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndId(Constants.NO, farmerId);

            if (farmerOptional.isPresent()) {
                Farmer farmer = farmerOptional.get();

                List<FarmProducts> farmProductsList = farmProductsRepository.findByDeletedFlag(Constants.NO);

                List<FarmProducts> farmProducts = farmProductsList.stream()
                        .filter(product -> product.getFarmer() == farmer).toList();

                List<FarmProductsResponse> farmProductsResponseList = farmProducts.stream()
                        .map(farmProduct -> modelMapper.map(farmProduct, FarmProductsResponse.class))
                        .collect(Collectors.toList());

                return new ApiResponse<>("Farmer's farm products fetched successfully.", farmProductsResponseList,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Farmer not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching farmer's farm products.", e);
            return new ApiResponse<>("An error occurred while fetching farmer's farm products.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    public ApiResponse<?> uploadProductToWarehouse(Long warehouseId, Long farmProductId, WHProductRequest whProductRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. User is not authenticated", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());
            if (farmerOptional.isEmpty()) {
                return new ApiResponse<>("Farmer Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            Farmer farmer = farmerOptional.get();
            Optional<Warehouse> warehouseOptional = warehouseRepo.findByIdAndDeletedFlag(warehouseId, Constants.NO);
            if (warehouseOptional.isEmpty()) {
                return new ApiResponse<>("Warehouse Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            Warehouse warehouse = warehouseOptional.get();
            Optional<FarmProducts> farmProductsOptional = farmProductsRepository.findByDeletedFlagAndId(Constants.NO, farmProductId);
            if (farmProductsOptional.isEmpty()) {
                return new ApiResponse<>("Product Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            FarmProducts farmProduct = farmProductsOptional.get();

            if (!Objects.equals(farmProduct.getFarmer(), farmer)) {
                return new ApiResponse<>("Farmer is not the owner of the product", null, HttpStatus.UNAUTHORIZED.value());
            }
            //upload the product to warehouse by creating a warehouse product

            WarehouseProduct warehouseProduct = new WarehouseProduct();
            warehouseProduct.setFarmProduct(farmProduct);
            warehouseProduct.setExpiryDate(whProductRequest.getExpiryDate());
            warehouseProduct.setAddedBy(currentUser);
            warehouseProduct.setPerishable(whProductRequest.isPerishable());
            warehouseProduct.setLowStockLimit(whProductRequest.getLowStockLimit());
            warehouseProduct.setAvailableUnits(warehouseProduct.getFarmProduct().getUnitsAvailable());
            warehouseProduct.getFarmProduct().setOnStock(warehouseProduct.getAvailableUnits() > whProductRequest.getLowStockLimit());
            warehouseProduct.setWarehouse(warehouse);
            warehouseProduct.setStatus(WHProductStatus.NOT_CHECKED);
            warehouseProduct.getFarmProduct().setSellingPoint(SellingPoint.WAREHOUSE);
            WarehouseProduct savedWhProduct = warehouseProductRepo.save(warehouseProduct);
            WHProductResponse whProductResponse = modelMapper.map(savedWhProduct, WHProductResponse.class);
            return new ApiResponse<>("Success Uploading the Product", whProductResponse, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error uploading product to warehouse", e);
            return new ApiResponse<>("Error uploading product to warehouse", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private boolean hasPermissionToUpdateProduct(FarmProducts farmProduct, User currentUser) {
        return farmProduct.getFarmer().getUser().getId().equals(currentUser.getId());
    }

    public ApiResponse<?> updateFarmProduct(FarmProductsRequest farmProductsRequest, Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<FarmProducts> farmProductOptional = farmProductsRepository.findByDeletedFlagAndId(Constants.NO,
                    id);

            if (farmProductOptional.isPresent()) {
                FarmProducts farmProduct = farmProductOptional.get();

                if (hasPermissionToUpdateProduct(farmProduct, currentUser)) {
                    updateProductFields(farmProduct, farmProductsRequest);

                    if (farmProductsRequest.getTypeOfProductId() != null) {
                        Optional<TypesOfProducts> typeOfProductOptional = typeOfProductsRepository
                                .findByDeletedFlagAndId(Constants.NO, farmProductsRequest.getTypeOfProductId());

                        if (typeOfProductOptional.isPresent()) {
                            TypesOfProducts typesOfProducts = typeOfProductOptional.get();
                            farmProduct.setTypeOfProduct(typesOfProducts);
                            farmProduct.setCategory(typesOfProducts.getCategory());
                        } else {
                            return new ApiResponse<>("Farm product not found.", null,
                                    HttpStatus.BAD_REQUEST.value());
                        }
                    }

                    // typeOfProductOptional.ifPresent(farmProduct::setTypeOfProduct);

                    if (farmProductsRequest.getDisplayImages() != null
                            && !farmProductsRequest.getDisplayImages().isEmpty()) {
                        updateDisplayImages(farmProduct, farmProductsRequest.getDisplayImages());
                    }

                    FarmProducts updateFarmProduct = farmProductsRepository.save(farmProduct);

                    FarmProductsResponse farmProductsResponse = modelMapper.map(updateFarmProduct,
                            FarmProductsResponse.class);

                    return new ApiResponse<>("Farm product was successfully updated.", farmProductsResponse,
                            HttpStatus.OK.value());
                } else {
                    return new ApiResponse<>("You are not allowed to perform this action.", null,
                            HttpStatus.BAD_REQUEST.value());
                }
            } else {
                return new ApiResponse<>("Farm product not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating the farm product.", e);
            return new ApiResponse<>("An error occurred while updating the farm product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void updateProductFields(FarmProducts farmProduct, FarmProductsRequest farmProductsRequest) {
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmProductsRequest.getOnStock(),
                farmProduct::setOnStock, farmProduct::getOnStock);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmProductsRequest.getIsPreListed(),
                farmProduct::setIsPreListed, farmProduct::getIsPreListed);
        // FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmProductsRequest.getProductName(),
        // farmProduct::setProductName, farmProduct::getProductName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmProductsRequest.getProductImage(),
                farmProduct::setProductImage, farmProduct::getProductImage);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmProductsRequest.getLongitude(),
                farmProduct::setLongitude, farmProduct::getLongitude);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmProductsRequest.getLatitude(),
                farmProduct::setLatitude, farmProduct::getLatitude);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmProductsRequest.getDescription(),
                farmProduct::setProduct_Description, farmProduct::getProduct_Description);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmProductsRequest.getUnitsAvailable(),
                farmProduct::setUnitsAvailable, farmProduct::getUnitsAvailable);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmProductsRequest.getPricePerUnit(),
                farmProduct::setPricePerUnit, farmProduct::getPricePerUnit);
    }

    private void updateDisplayImages(FarmProducts farmProduct, List<DisplayImagesRequest> displayImagesRequestList) {
        Set<String> existingImageSet = farmProduct.getDisplayImages().stream()
                .map(DisplayImages::getDisplayImage)
                .collect(Collectors.toSet());

        List<DisplayImages> savedDisplayImages = displayImagesRequestList.stream()
                .map(displayImages -> {
                    String image = displayImages.getDisplayImages();
                    if (!existingImageSet.contains(image)) {
                        DisplayImages displayImage = new DisplayImages();
                        displayImage.setDisplayImage(image);
                        return displayImagesRepository.save(displayImage);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        farmProduct.getDisplayImages().addAll(savedDisplayImages);
    }

    public ApiResponse<?> removeDisplayImages(FarmProductDispImageRequest farmProductDispImageRequest, Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<FarmProducts> farmProductOptional = farmProductsRepository.findByDeletedFlagAndId(Constants.NO,
                    id);
            if (farmProductOptional.isPresent()) {
                FarmProducts farmProduct = farmProductOptional.get();

                if (hasPermissionToUpdateProduct(farmProduct, currentUser)) {
                    if (farmProductDispImageRequest.getDisplayImagesIds() != null
                            && !farmProductDispImageRequest.getDisplayImagesIds().isEmpty()) {
                        List<DisplayImages> displayImages = displayImagesRepository
                                .findAllById(farmProductDispImageRequest.getDisplayImagesIds());

                        if (!displayImages.isEmpty()) {
                            List<DisplayImages> filteredImages = displayImages.stream()
                                    .filter(images -> images.getDeletedFlag() == Constants.NO)
                                    .collect(Collectors.toList());

                            farmProduct.getDisplayImages().removeAll(filteredImages);
                        }
                    }

                    FarmProducts updatedFarmProduct = farmProductsRepository.save(farmProduct);

                    FarmProductsResponse farmProductsResponse = modelMapper.map(updatedFarmProduct,
                            FarmProductsResponse.class);

                    return new ApiResponse<>("Display image was successfully removed.", farmProductsResponse,
                            HttpStatus.OK.value());
                } else {
                    return new ApiResponse<>("You are not allowed to perform this action.", null,
                            HttpStatus.BAD_REQUEST.value());
                }

            } else {
                return new ApiResponse<>("Farm Product not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while removing display image.", e);
            return new ApiResponse<>("An error occurred while removing display image.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> farmProductDelete(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<FarmProducts> farmProductOptional = farmProductsRepository.findByDeletedFlagAndId(Constants.NO,
                    id);
            if (farmProductOptional.isPresent()) {
                FarmProducts farmProduct = farmProductOptional.get();

                farmProduct.setDeletedAt(LocalDateTime.now());
                farmProduct.setDeletedBy(currentUser);
                farmProduct.setDeletedFlag(Constants.YES);

                FarmProducts deletedFarmProduct = farmProductsRepository.save(farmProduct);

                return new ApiResponse<>("Farm product was successfully deleted.", null, HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Farm Product not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting farm product.", e);
            return new ApiResponse<>("An error occurred while deleting farm product.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    public ResponseEntity<?> applyDiscount(
            @PathVariable Long farmProductId
    ) {
        try {
            Optional<FarmProducts> productOptional = farmProductsRepository.findById(farmProductId);

            if (productOptional.isPresent()) {
                FarmProducts product = productOptional.get();
                product.setDiscount(discount);
                farmProductsRepository.save(product);

                return ResponseEntity.ok("Discount applied successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {

            log.error("An error occurred while applying a discount.", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while applying the discount.");
        }
    }


}


