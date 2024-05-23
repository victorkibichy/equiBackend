package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Inventory;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Inventory.DTO.InventoryResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.FarmProductsResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.UnitsRequest;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsRepository;
import com.EquiFarm.EquiFarm.OrderItem.OrderItem;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.net.http.HttpClient;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepo inventoryRepo;
    private final FarmProductsRepository farmProductsRepository;
    private final ModelMapper modelMapper;

    public void createInventory(FarmProducts farmProducts) {
        Inventory inventory = Inventory.builder()
                .farmProducts(farmProducts)
                .availableUnits(farmProducts.getUnitsAvailable())
                .reservedUnits(0.0)
                .soldUnits(0.0)
                .build();
        Inventory savedInventory = inventoryRepo.save(inventory);
        log.info("New Inventory available units...." + savedInventory.getAvailableUnits());
        log.info("New Inventory reserved units....." + savedInventory.getReservedUnits());
        log.info("New Inventory sold units....." + savedInventory.getSoldUnits());

    }
    public void addAvailableUnits(FarmProducts farmProducts, Double units){
        Optional<Inventory> inventoryOptional = inventoryRepo.findByFarmProducts(farmProducts);
        if(inventoryOptional.isPresent()){
            Inventory inventory = inventoryOptional.get();
            System.out.println("Available available: " + inventory.getAvailableUnits());
            inventory.setAvailableUnits(inventory.getAvailableUnits() + units);
            inventoryRepo.save(inventory);
            System.out.println("Available after adding Units: "+ inventory.getAvailableUnits());
        } else{
            log.info("Inventory not found");
        }
    }
    public Boolean productAvailable(FarmProducts farmProducts, Integer units) {
        Optional<Inventory> inventoryOptional = inventoryRepo.findByFarmProducts(farmProducts);
        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            System.out.println("Available after checking availability" + inventory.getAvailableUnits());
            System.out.println("Reserved after checking availability" + inventory.getReservedUnits());
            System.out.println("Sold after checking availability" + inventory.getSoldUnits());
            return inventory.getAvailableUnits() > units && inventory.getAvailableUnits() > 1;
        }
        log.info("Inventory for availability not found");
        return false;
    }
    public void reserveUnits(FarmProducts farmProducts, Integer units){
        Optional<Inventory> inventoryOptional = inventoryRepo.findByFarmProducts(farmProducts);
        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            inventory.setAvailableUnits(inventory.getAvailableUnits() - units);
            inventory.setReservedUnits(inventory.getReservedUnits() + Double.valueOf(units));
            Inventory savedInventory = inventoryRepo.save(inventory);
            if (savedInventory.getAvailableUnits() == 1){
                farmProducts.setOnStock(false);
                farmProductsRepository.save(farmProducts);
                System.out.println("stock status If available is 1:" + farmProducts.getOnStock());
                // TODO: notify owner to add available units
            }
            System.out.println("Available after reservation"+ inventory.getAvailableUnits());
            System.out.println("Reserved after reservation" + inventory.getReservedUnits());
            System.out.println("stock status:" + farmProducts.getOnStock());
        } else {
            log.info("Inventory for reserved not found");
        }
    }
    public void releaseReservedUnits(OrderItem orderItem){
        Optional<Inventory> inventoryOptional = inventoryRepo.findByFarmProducts(orderItem.getFarmProduct());
        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            inventory.setAvailableUnits(inventory.getAvailableUnits() + orderItem.getQuantity());
            inventory.setReservedUnits(inventory.getReservedUnits() - Double.valueOf(orderItem.getQuantity()));
            inventoryRepo.save(inventory);
            System.out.println("Available after releasing reservation"+ inventory.getAvailableUnits());
            System.out.println("Reserved after releasing reservation" + inventory.getReservedUnits());
        } else {
            log.info("Inventory for release reserved not found");
        }
    }
    public void soldUnits(FarmProducts farmProducts, Integer soldUnits){
        Optional<Inventory> inventoryOptional = inventoryRepo.findByFarmProducts(farmProducts);
        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            inventory.setReservedUnits(inventory.getReservedUnits() - Double.valueOf(soldUnits));
            inventory.setSoldUnits(Double.valueOf(soldUnits));
            inventoryRepo.save(inventory);
            farmProducts.setUnitsAvailable(farmProducts.getUnitsAvailable() - Double.valueOf(soldUnits));
            FarmProducts farmProducts1 = farmProductsRepository.save(farmProducts);
            System.out.println("Available in Products"+ farmProducts1.getUnitsAvailable());
            System.out.println("Available after selling"+ inventory.getAvailableUnits());
            System.out.println("Reserved after selling" + inventory.getReservedUnits());
            System.out.println("Sold units" + inventory.getSoldUnits());
        } else {
            log.info("Inventory for sold not found");
        }
    }
    public ApiResponse<?> getInventory(Long productId){
        try {
            Optional<FarmProducts> farmProductsOptional  = farmProductsRepository.findByDeletedFlagAndId(Constants.NO, productId);
            if(farmProductsOptional.isEmpty()){
                return  new ApiResponse<>("Farm Product not found", null, HttpStatus.NOT_FOUND.value());
            }
            FarmProducts farmProducts = farmProductsOptional.get();
            Optional<Inventory> inventoryOptional = inventoryRepo.findByFarmProducts(farmProducts);
            if (inventoryOptional.isEmpty()){
                return new ApiResponse<>("Inventory Not found", null, HttpStatus.NOT_FOUND.value());
            }
            Inventory inventory = inventoryOptional.get();
            InventoryResponse inventoryResponse = modelMapper.map(inventory, InventoryResponse.class);
            return new ApiResponse<>("Success fetching inventory", inventoryResponse, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Eror: ", e);
            return new ApiResponse<>("Error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> updateAvailableUnits(UnitsRequest unitsRequest){
        Optional<FarmProducts> farmProductsOptional = farmProductsRepository.findByDeletedFlagAndId(Constants.NO, unitsRequest.getProductId());
        if (farmProductsOptional.isEmpty()){
            return new ApiResponse<>("Farm product not found", null, HttpStatus.NOT_FOUND.value());
        }
        FarmProducts farmProducts = farmProductsOptional.get();
        farmProducts.setUnitsAvailable(farmProducts.getUnitsAvailable() + unitsRequest.getUnits());
        farmProducts.setOnStock(true);
        FarmProducts savedFarmProducts = farmProductsRepository.save(farmProducts);
        addAvailableUnits(farmProducts, unitsRequest.getUnits());
        FarmProductsResponse farmProductsResponse = modelMapper.map(savedFarmProducts, FarmProductsResponse.class);
        return new ApiResponse<>("Success adding product", farmProductsResponse, HttpStatus.OK.value());
    }

}