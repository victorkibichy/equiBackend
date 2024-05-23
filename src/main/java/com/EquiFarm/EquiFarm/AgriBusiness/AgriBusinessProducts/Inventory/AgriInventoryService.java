package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.Inventory;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrderItem.AgriBusinessOrderItem;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProduct;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductRepository;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.DTO.AgriBusinessProductResponse;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.Inventory.DTO.AgriInventoryResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.UnitsRequest;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgriInventoryService {
    private final AgriInventoryRepo agriInventoryRepo;
    private final AgriBusinessProductRepository agribusinessRepository;
    private final ModelMapper modelMapper;

    public void createAgriInventory(AgriBusinessProduct agriBusinessProduct) {
        AgriInventory inventory = AgriInventory.builder()
                .agriBusinessProduct(agriBusinessProduct)
                .availableUnits(agriBusinessProduct.getUnitsAvailable())
                .reservedUnits(0.0)
                .soldUnits(0.0)
                .build();
        AgriInventory savedAgriInventory = agriInventoryRepo.save(inventory);
        log.info("New AgriInventory available units...." + savedAgriInventory.getAvailableUnits());
        log.info("New AgriInventory reserved units....." + savedAgriInventory.getReservedUnits());
        log.info("New AgriInventory sold units....." + savedAgriInventory.getSoldUnits());

    }
    public void addAvailableUnits(AgriBusinessProduct agriBusinessProduct, Double units){
        Optional<AgriInventory> inventoryOptional = agriInventoryRepo.findByAgriBusinessProduct(agriBusinessProduct);
        if(inventoryOptional.isPresent()){
            AgriInventory inventory = inventoryOptional.get();
            System.out.println("Available available: " + inventory.getAvailableUnits());
            inventory.setAvailableUnits(inventory.getAvailableUnits() + units);
            agriInventoryRepo.save(inventory);
            System.out.println("Available after adding Units: "+ inventory.getAvailableUnits());
        } else{
            log.info("AgriInventory not found");
        }
    }
    public Boolean productAvailable(AgriBusinessProduct agribusiness, Integer units) {
        Optional<AgriInventory> inventoryOptional = agriInventoryRepo.findByAgriBusinessProduct(agribusiness);
        if (inventoryOptional.isPresent()) {
            AgriInventory inventory = inventoryOptional.get();
            System.out.println("Available after checking availability" + inventory.getAvailableUnits());
            System.out.println("Reserved after checking availability" + inventory.getReservedUnits());
            System.out.println("Sold after checking availability" + inventory.getSoldUnits());
            return inventory.getAvailableUnits() > units && inventory.getAvailableUnits() > 1;
        }
        log.info("AgriInventory for availability not found");
        return false;
    }
    public void reserveUnits(AgriBusinessProduct agribusiness, Integer units){
        Optional<AgriInventory> inventoryOptional = agriInventoryRepo.findByAgriBusinessProduct(agribusiness);
        if (inventoryOptional.isPresent()) {
            AgriInventory inventory = inventoryOptional.get();
            inventory.setAvailableUnits(inventory.getAvailableUnits() - units);
            inventory.setReservedUnits(inventory.getReservedUnits() + Double.valueOf(units));
            AgriInventory savedAgriInventory = agriInventoryRepo.save(inventory);
            if (savedAgriInventory.getAvailableUnits() == 1){
                agribusiness.setOnStock(false);
                agribusinessRepository.save(agribusiness);
                System.out.println("stock status If available is 1:" + agribusiness.getOnStock());
                // TODO: notify owner to add available units
            }
            System.out.println("Available after reservation"+ inventory.getAvailableUnits());
            System.out.println("Reserved after reservation" + inventory.getReservedUnits());
            System.out.println("stock status:" + agribusiness.getOnStock());
        } else {
            log.info("AgriInventory for reserved not found");
        }
    }
    public void releaseReservedUnits(AgriBusinessOrderItem orderItem){
        Optional<AgriInventory> inventoryOptional = agriInventoryRepo.findByAgriBusinessProduct(orderItem.getAgriBusinessProduct());
        if (inventoryOptional.isPresent()) {
            AgriInventory inventory = inventoryOptional.get();
            inventory.setAvailableUnits(inventory.getAvailableUnits() + orderItem.getQuantity());
            inventory.setReservedUnits(inventory.getReservedUnits() - Double.valueOf(orderItem.getQuantity()));
            agriInventoryRepo.save(inventory);
            System.out.println("Available after releasing reservation"+ inventory.getAvailableUnits());
            System.out.println("Reserved after releasing reservation" + inventory.getReservedUnits());
        } else {
            log.info("AgriInventory for release reserved not found");
        }
    }
    public void soldUnits(AgriBusinessProduct agribusiness, Integer soldUnits){
        Optional<AgriInventory> inventoryOptional = agriInventoryRepo.findByAgriBusinessProduct(agribusiness);
        if (inventoryOptional.isPresent()) {
            AgriInventory inventory = inventoryOptional.get();
            inventory.setReservedUnits(inventory.getReservedUnits() - Double.valueOf(soldUnits));
            inventory.setSoldUnits(Double.valueOf(soldUnits));
            agriInventoryRepo.save(inventory);
            System.out.println("Available after selling"+ inventory.getAvailableUnits());
            System.out.println("Reserved after selling" + inventory.getReservedUnits());
            System.out.println("Sold units" + inventory.getSoldUnits());
        } else {
            log.info("AgriInventory for sold not found");
        }
    }
    public ApiResponse<?> getAgriInventory(Long productId){
        try {
            Optional<AgriBusinessProduct> agribusinessOptional  = agribusinessRepository.findByDeletedFlagAndId(Constants.NO, productId);
            if(agribusinessOptional.isEmpty()){
                return  new ApiResponse<>("Farm Product not found", null, HttpStatus.NOT_FOUND.value());
            }
            AgriBusinessProduct agribusiness = agribusinessOptional.get();
            Optional<AgriInventory> inventoryOptional = agriInventoryRepo.findByAgriBusinessProduct(agribusiness);
            if (inventoryOptional.isEmpty()){
                return new ApiResponse<>("AgriInventory Not found", null, HttpStatus.NOT_FOUND.value());
            }
            AgriInventory inventory = inventoryOptional.get();
            AgriInventoryResponse inventoryResponse = modelMapper.map(inventory, AgriInventoryResponse.class);
            return new ApiResponse<>("Success fetching inventory", inventoryResponse, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Eror: ", e);
            return new ApiResponse<>("Error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> updateAvailableUnits(UnitsRequest unitsRequest){
        Optional<AgriBusinessProduct> agribusinessOptional = agribusinessRepository.findByDeletedFlagAndId(Constants.NO, unitsRequest.getProductId());
        if (agribusinessOptional.isEmpty()){
            return new ApiResponse<>("Farm product not found", null, HttpStatus.NOT_FOUND.value());
        }
        AgriBusinessProduct agribusiness = agribusinessOptional.get();
        agribusiness.setUnitsAvailable(agribusiness.getUnitsAvailable() + unitsRequest.getUnits());
        agribusiness.setOnStock(true);
        AgriBusinessProduct savedAgriBusinessProduct = agribusinessRepository.save(agribusiness);
        addAvailableUnits(agribusiness, unitsRequest.getUnits());
        AgriBusinessProductResponse agribusinessResponse = modelMapper.map(savedAgriBusinessProduct, AgriBusinessProductResponse.class);
        return new ApiResponse<>("Success adding product", agribusinessResponse, HttpStatus.OK.value());
    }

}