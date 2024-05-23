package com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.FarmProductsResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsRepository;
import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Farmer.FarmerRepository;
import com.EquiFarm.EquiFarm.Warehouse.Warehouse;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.DTO.WHProductRequest;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.DTO.WHProductResponse;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.*;
//import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.WHProductCheck.WHProductCheckRepo;
//import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.WHProductCheck.WarehouseProductCheck;
//import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.WHProductCheck.WarehouseProductCheckKey;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.DTO.QualityCheckRequest;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseRepo;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.String.valueOf;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class WarehouseProductService {
    private final WarehouseRepo warehouseRepo;
    private final WarehouseProductRepo warehouseProductRepo;
    private final FarmerRepository farmerRepository;
    private final FarmProductsRepository farmProductsRepository;
    private final QualityCheckRepo qualityCheckRepo;
    private final ModelMapper modelMapper;
    private static final Map<String, Integer> categoryCountMap = new HashMap<>();

    public ApiResponse<?> getAllProductsForWarehouse(LocalDate date,
                                                     Boolean qualityCheckDone,
                                                     Boolean verified,
                                                     Boolean perishable,
                                                     Long farmerId,
                                                     Long typeOProductId,
                                                     Long productCategoryId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. User is unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<Warehouse> warehouseOptional = warehouseRepo.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());
            if (warehouseOptional.isEmpty()) {
                return new ApiResponse<>("Warehouse not found", null, HttpStatus.NOT_FOUND.value());
            }
            Warehouse warehouse = warehouseOptional.get();
            List<WarehouseProduct> warehouseProductList = warehouseProductRepo.findByWarehouseAndDeletedFlag(warehouse, Constants.NO);
            List<WHProductResponse> whProductResponses = warehouseProductList
                    .stream()
                    .filter(product -> (date == null || product.getExpiryDate().equals(date)))
                    .filter(product -> (qualityCheckDone == null || product.isQualityChecked() == qualityCheckDone))
                    .filter(product -> (verified == null || product.isVerified() == verified))
                    .filter(product -> (perishable == null || product.isPerishable() == perishable))
                    .filter(product -> (farmerId == null || product.getFarmProduct().getFarmer().getId().equals(farmerId)))
                    .filter(product -> (productCategoryId == null || product.getFarmProduct().getCategory().getId().equals(productCategoryId)))
                    .filter(product -> (typeOProductId == null || product.getFarmProduct().getTypeOfProduct().getId().equals(typeOProductId)))

                    .map(prod -> modelMapper.map(prod, WHProductResponse.class))
                    .toList();
            return new ApiResponse<>("Success fetching warehouse products", whProductResponses, HttpStatus.OK.value());
        } catch (Exception e) {
            log.info("Error fetching products", e);
            return new ApiResponse<>("Error fetching products", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    //must be available and verified and Todo: not past expiry date
    public ApiResponse<?> getAllProductsForMarketPlace(Long warehouseId, Long typeOProductId, Long productCategoryId, Boolean perishable) {
        try {
            List<WarehouseProduct> warehouseProductList = warehouseProductRepo.findByVerifiedAndAvailableAndDeletedFlag(true, true, Constants.NO);
            List<FarmProducts> farmProductsList = warehouseProductList.stream().map(warehouseProduct -> modelMapper
                    .map(warehouseProduct, FarmProducts.class))
                    .toList();
//             Filter the list to get FarmProducts with a one-to-one relationship with WarehouseProducts
            List<FarmProducts> oneToOneFarmProductsList = farmProductsList.stream()
                    .filter(farmProduct -> Collections.frequency(farmProductsList, farmProduct) == 1)
                    .toList();

            List<FarmProductsResponse> farmProductsResponses = warehouseProductList
                    .stream()
                    .filter(product -> (warehouseId == null || product.getWarehouse().getId().equals(warehouseId)))
                    .filter(product -> (typeOProductId == null || product.getFarmProduct().getTypeOfProduct().getId().equals(typeOProductId)))
                    .filter(product -> (productCategoryId == null || product.getFarmProduct().getCategory().getId().equals(productCategoryId)))
                    .filter(product -> (perishable == null || product.isPerishable() == perishable))
                    .map(warehouseProduct -> modelMapper
                            .map(warehouseProduct, FarmProductsResponse.class))
                    .toList();
            return new ApiResponse<>("Success fetching warehouse products", farmProductsResponses, HttpStatus.OK.value());
        } catch (Exception e) {
            log.info("Error Fetching Warehouse Products", e);
            return new ApiResponse<>("Error Fetching Warehouse Products", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getProductById(Long whProductId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied! User is not authenticated", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<Warehouse> warehouseOptional = warehouseRepo.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());
            if (warehouseOptional.isEmpty()) {
                return new ApiResponse<>("Warehouse Owner Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            Warehouse warehouse = warehouseOptional.get();
            Optional<WarehouseProduct> warehouseProductOptional = warehouseProductRepo.findByIdAndDeletedFlag(whProductId, Constants.NO);
            if (warehouseProductOptional.isEmpty()) {
                return new ApiResponse<>("Warehouse Product not found", null, HttpStatus.NOT_FOUND.value());
            }
            WarehouseProduct warehouseProduct = warehouseProductOptional.get();
            if (!Objects.equals(warehouse, warehouseProduct.getWarehouse())) {
                return new ApiResponse<>("Does not own the warehouse product", null, HttpStatus.UNAUTHORIZED.value());
            }
            WHProductResponse whProductResponse = modelMapper.map(warehouseProduct,
                    WHProductResponse.class);
            return new ApiResponse<>("Success Fetching Warehouse product", whProductResponse, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error fetching warehouse product", e);
            return new ApiResponse<>("Error fetching warehouse product", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> performQualityChecks(Long whProductId, List<QualityCheckRequest> checkRequest) {
        try {
            Optional<WarehouseProduct> warehouseProductOptional = warehouseProductRepo.findByIdAndDeletedFlag(whProductId, Constants.NO);
            if (warehouseProductOptional.isEmpty()) {
                return new ApiResponse<>("Warehouse Product Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            WarehouseProduct warehouseProduct = warehouseProductOptional.get();
            for(QualityCheckRequest qualityCheckRequest: checkRequest) {
                QualityCheck newQualityCheck = new QualityCheck();
                newQualityCheck.setCheckName(qualityCheckRequest.getCheckName());
                newQualityCheck.setDescription(qualityCheckRequest.getDescription());
                newQualityCheck.setQualityCheckResult(qualityCheckRequest.getQualityCheckResult());
                newQualityCheck.setComments(qualityCheckRequest.getComments());
                List<QualityCheck> qualityChecks = warehouseProduct.getQualityChecks();
                qualityChecks.add(newQualityCheck);
                warehouseProduct.setQualityChecks(qualityChecks);
            }
            verifyWHProduct(whProductId);

// Also add the warehouseProduct to the qualityCheck's warehouseProducts list
//            List<WarehouseProduct> warehouseProducts = qualityCheck.getWarehouseProducts();
//            warehouseProducts.add(warehouseProduct);
//            qualityCheck.setWarehouseProducts(warehouseProducts);
//            WarehouseProduct testedProduct = warehouseProductRepo.save(warehouseProduct);
//            WarehouseResponse warehouseResponse = modelMapper.map(testedProduct, WarehouseResponse.class);
            return new ApiResponse<>("Success performing the test", null, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error uploading product to warehouse", e);
            return new ApiResponse<>("Error Performing  Test on  product", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> verifyWHProduct(Long whProductId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            Optional<WarehouseProduct> warehouseProductOptional = warehouseProductRepo.findByIdAndDeletedFlag(whProductId, Constants.NO);
            if (warehouseProductOptional.isEmpty()) {
                return new ApiResponse<>("Warehouse Product Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            WarehouseProduct warehouseProduct = warehouseProductOptional.get();
            List<QualityCheck> qualityCheckList = warehouseProduct.getQualityChecks();
            if (qualityCheckList.isEmpty()){
                return new ApiResponse<>("No Quality Tests Done yet", null, HttpStatus.UNAUTHORIZED.value());
            }
            for (QualityCheck qualityCheck: qualityCheckList)
            {
                    if (QualityCheckResult.FAIL.equals(qualityCheck.getQualityCheckResult())) {
                        warehouseProduct.setVerified(false);
                        warehouseProduct.setQualityChecked(true);
                        break; // No need to continue checking if a failure is found
                }
                warehouseProduct.setVerified(true);
                warehouseProduct.setQualityChecked(true);
            }
            WarehouseProduct savedWhProduct = warehouseProductRepo.save(warehouseProduct);
            WHProductResponse whProductResponse = modelMapper.map(savedWhProduct, WHProductResponse.class);
            return new ApiResponse<>("Success Verifying the Product", whProductResponse, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error verifying warehouse product", e);
            return new ApiResponse<>("Error verifying warehouse product", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    private void ReduceStock(WarehouseProduct whProduct, Double soldProducts) {
        whProduct.setAvailableUnits(whProduct.getAvailableUnits() - soldProducts);
        if (whProduct.getAvailableUnits() < whProduct.getLowStockLimit()) {
            //TODO: notify warehouse and farmer of the low limit
            whProduct.getFarmProduct().setOnStock(false);
        }
        warehouseProductRepo.save(whProduct);
    }

    //Todo: Sell from Warehouse
    public ApiResponse<?> sellProductFromWarehouse(Long whProductId) {
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
            Optional<WarehouseProduct> warehouseProductOptional = warehouseProductRepo.findByVerifiedAndIdAndDeletedFlag(true, whProductId, Constants.NO);
            if (warehouseProductOptional.isEmpty()) {
                return new ApiResponse<>("Warehouse product not found", null, HttpStatus.NOT_FOUND.value());
            }
            WarehouseProduct warehouseProduct = warehouseProductOptional.get();
            if (!Objects.equals(farmer, warehouseProduct.getFarmProduct().getFarmer())) {
                return new ApiResponse<>("Not the owner of the product", null, HttpStatus.UNAUTHORIZED.value());
            }
//            if (warehouseProduct.getExpiryDate().isBefore(LocalDate.now())){
//                warehouseProduct.setStatus(WHProductStatus.EXPIRED);
//                return new ApiResponse<>("Product is expired", null, HttpStatus.NOT_MODIFIED.value());
//            }
            warehouseProduct.setAvailable(true);
            WarehouseProduct savedWhProduct = warehouseProductRepo.save(warehouseProduct);
            WHProductResponse whProductResponse = modelMapper.map(savedWhProduct, WHProductResponse.class);
            return new ApiResponse<>("Success Uploading the Product", whProductResponse, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error uploading product to warehouse", e);
            return new ApiResponse<>("Error uploading product to warehouse", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    //Todo: filter by returned, rejected,expired
    public ApiResponse<?> findByStatus(WHProductStatus status) {
        try {
            List<WarehouseProduct> warehouseProductList = warehouseProductRepo.findByQualityCheckedAndStatusAndDeletedFlag(true, status, Constants.NO);
            List<WHProductResponse> whProductResponses = warehouseProductList
                    .stream()
                    .map(product -> modelMapper
                            .map(product, WHProductResponse.class))
                    .toList();
            return new ApiResponse<>("Success fetching products", whProductResponses, HttpStatus.OK.value());
        } catch (Exception e) {
            log.info("Error occurred while fetching products", e);
            return new ApiResponse<>("Error occurred while fetching products", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /*TODO: OrderByExpiry Date
       Remove the expired products from database
       Notify Warehouse and Farmer
       For Perishable goods:
       If expiration date is 10...7 days away notify, otherwise alert
       For Non-perishable goods:
       Notify 5 days away
       If non perishable product is nearing expiration date maybe a month,
       notify farmer to sell it*/
//    @Scheduled(fixedDelay = 15000)
    public void orderByDates() {
        try {
//            User currentUser = SecurityUtils.getCurrentUser();
//            if (currentUser == null) {
//                throw new IllegalStateException("Unauthorized User");
//            }
//            Optional<Warehouse> warehouseOptional = warehouseRepo.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());
//            if (warehouseOptional.isEmpty()) {
//                throw new IllegalStateException("Warehouse Not Found");
//            }
//            Warehouse warehouse = warehouseOptional.get();
            List<WarehouseProduct> warehouseProductList = warehouseProductRepo.findByQualityCheckedAndDeletedFlag(true, Constants.NO);
            LocalDate today = LocalDate.now();
            LocalDate fourDaysFromNow = today.plusDays(4);
            LocalDate twoDaysFromNow = today.plusDays(2);
            for (WarehouseProduct warehouseProduct : warehouseProductList) {
                if (warehouseProduct.getExpiryDate().equals(today)) {
                    //TODO: delete Warehouse Product
//                    System.out.println("Deleting product " + warehouseProduct.getId() + " Coz it has expired");
//                    warehouseProduct.setStatus(WHProductStatus.EXPIRED);
//                    warehouseProduct.setAvailable(false);
//                    warehouseProduct.setDeletedAt(LocalDateTime.now());
//                    warehouseProduct.setDeletedFlag(Constants.YES);
//                    warehouseProductRepo.save(warehouseProduct);
//                    System.out.println("Warehouse Product "+ warehouseProduct.getId() + " Deleted");

                }
                if (warehouseProduct.getExpiryDate().isAfter(twoDaysFromNow) &&
                        warehouseProduct.getExpiryDate().isBefore(fourDaysFromNow)) {
                    //Todo: Send Reminder Notifications
//                    System.out.println("product " + warehouseProduct.getId() + " expires in 4 days...");
                }
                if (warehouseProduct.getExpiryDate().isAfter(today) &&
                        warehouseProduct.getExpiryDate().isBefore(twoDaysFromNow)) {
                    //TODO: Send Expiry Alerts
//                    System.out.println("Receiving alerts coz product " + warehouseProduct.getId() + " expiring in 2 days");
                }
            }
        } catch (Exception e) {
            log.info("Error Occurred", e);
        }
    }

    public void updateExpiredProducts(WarehouseProduct warehouseProduct) {
        warehouseProduct.setStatus(WHProductStatus.EXPIRED);
        warehouseProduct.setAvailable(false);
        warehouseProduct.setDeletedAt(LocalDateTime.now());
//            warehouseProduct.setDeletedBy(currentUser);
        warehouseProduct.setDeletedFlag(Constants.YES);
        System.out.println("Warehouse Product " + warehouseProduct.getId() + "Deleted");
        warehouseProductRepo.save(warehouseProduct);
    }

    public String generateProductLabel(String categoryName) {
        categoryName = categoryName.trim().toUpperCase();
        int count = categoryCountMap.getOrDefault(categoryName, 1);
        String uniqueId = categoryName + "-" + String.format("%03d", count);
        categoryCountMap.put(categoryName, count + 1);
        return uniqueId;
    }

    public ApiResponse<?> updateWHProduct(Long whProductId, WHProductRequest whProductRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. User is unAuthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<Warehouse> warehouseOptional = warehouseRepo.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());
            if (warehouseOptional.isEmpty()) {
                return new ApiResponse<>("Warehouse Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            Optional<WarehouseProduct> warehouseProductOptional = warehouseProductRepo.findByIdAndDeletedFlag(whProductId, Constants.NO);
            if (warehouseProductOptional.isEmpty()) {
                return new ApiResponse<>("Warehouse Product Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            WarehouseProduct warehouseProduct = warehouseProductOptional.get();

            //Todo: Who should update the product? Farmer or warehouse owner?
            return new ApiResponse<>("Success Updating the Warehouse Product", warehouseProduct, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error updating warehouse product", e);
            return new ApiResponse<>("Error updating warehouse product", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> deleteWHProduct(Long whProduct) {
        try {
//            User currentUser = SecurityUtils.getCurrentUser();
//            if (currentUser == null) {
//                return new ApiResponse<>("Access Denied. User is unAuthenticated", null, HttpStatus.UNAUTHORIZED.value());
//            }
//            Optional<Warehouse> warehouseOptional = warehouseRepo.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());
//            if (warehouseOptional.isEmpty()) {
//                return new ApiResponse<>("Warehouse Not Found", null, HttpStatus.NOT_FOUND.value());
//            }
//            Warehouse warehouse = warehouseOptional.get();
            Optional<WarehouseProduct> warehouseProductOptional = warehouseProductRepo.findByIdAndDeletedFlag(whProduct, Constants.NO);
            if (warehouseProductOptional.isEmpty()) {
                return new ApiResponse<>("Warehouse product Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            WarehouseProduct warehouseProduct = warehouseProductOptional.get();
//            if (!Objects.equals(warehouse, warehouseProduct.getWarehouse())) {
//                return new ApiResponse<>("Not the Owner of the warehouse Product", null, HttpStatus.UNAUTHORIZED.value());
//            }
            warehouseProduct.setDeletedAt(LocalDateTime.now());
//            warehouseProduct.setDeletedBy(currentUser);
            warehouseProduct.setDeletedFlag(Constants.YES);
            warehouseProductRepo.save(warehouseProduct);
            System.out.println("Product " + warehouseProduct.getId() + " Has been deleted");
            return new ApiResponse<>("Success deleting Warehouse product", null, HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            log.info("Error Occurred when deleting a warehouse product: ", e);
            return new ApiResponse<>("Error Occurred when deleting a warehouse product", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
