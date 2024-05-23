package com.EquiFarm.EquiFarm.Farmer.Farm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Farmer.FarmerRepository;
import com.EquiFarm.EquiFarm.Farmer.DTO.FarmersResponse;
import com.EquiFarm.EquiFarm.Farmer.Farm.DTO.FarmRequest;
import com.EquiFarm.EquiFarm.Farmer.Farm.DTO.FarmResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;

import lombok.extern.slf4j.Slf4j;
import lombok.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmService {

    private final FarmRepository farmRepository;
    private final FarmerRepository farmerRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> getFarmByFarmId(Long id) {
        try {
            Optional<Farm> farmOptional = farmRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (farmOptional.isPresent()) {
                Farm farm = farmOptional.get();
                FarmResponse farmResponse = modelMapper.map(farm, FarmResponse.class);

                return new ApiResponse<FarmResponse>("Farm fetched successfully", farmResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Farm not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching farm", e);

            return new ApiResponse<>("An error occurred while fetching farm.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    // TODO: Get all types of products

    @Transactional
    public ApiResponse<?> getFarmByFarmerId(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                log.info("Current admin: {}", currentUser);
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (farmerOptional.isPresent()) {
                Farmer farmer = farmerOptional.get();

                List<Farm> farmList = farmRepository.findByDeletedFlag(Constants.NO);

                // Filter the list of farms to get all the farms that the farmer is in
                List<Farm> farmsByFarmer = farmList.stream()
                        .filter(farm -> farm.getFarmers().contains(farmer))
                        .collect(Collectors.toList());

                List<FarmResponse> farmResponseList = farmsByFarmer.stream()
                        .map(farm -> modelMapper.map(farm, FarmResponse.class))
                        .collect(Collectors.toList());

                return new ApiResponse<>("Farmer's farms fetched successfully.", farmResponseList,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Farmer not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching farmer's farms.", e);
            return new ApiResponse<>("An error occurred while fetching farmer's farms.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> updateFarm(FarmRequest farmRequest, Long id) {
        try {

            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Farm> farmOptional = farmRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (farmOptional.isPresent()) {
                Farm farm = farmOptional.get();

                updateFarmFields(farmRequest, farm);

                updateFarmOwners(farmRequest.getFarmersIds(), farm);

                Farm updatedFarm = farmRepository.save(farm);
                FarmResponse farmResponse = modelMapper.map(updatedFarm, FarmResponse.class);
                return new ApiResponse<>("Farm was successfully updated.", farmResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Farm not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating the farm.", e);
            return new ApiResponse<>("An error occurred while updating the farm.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void updateFarmFields(FarmRequest farmRequest, Farm farm) {
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmRequest.getTypeOfOwnership(),
                farm::setTypeOfOwnership,
                farm::getTypeOfOwnership);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmRequest.getTitleNumber(),
                farm::setTitleNumber,
                farm::getTitleNumber);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmRequest.getTitleDeed(),
                farm::setTitleDeed,
                farm::getTitleDeed);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmRequest.getFarmSize(),
                farm::setFarmSize,
                farm::getFarmSize);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmRequest.getLatitude(),
                farm::setLatitude,
                farm::getLatitude);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmRequest.getLongitude(),
                farm::setLongitude,
                farm::getLongitude);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmRequest.getFarmIncome(),
                farm::setFarmIncome,
                farm::getFarmIncome);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmRequest.getSoilType(),
                farm::setSoilType,
                farm::getSoilType);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmRequest.getSourceOfWater(),
                farm::setSourceOfWater,
                farm::getSourceOfWater);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmRequest.getFarmHistory(),
                farm::setFarmHistory,
                farm::getFarmHistory);
    }

    private void updateFarmOwners(List<Long> farmerIds, Farm farm) {
        if (farmerIds != null && !farmerIds.isEmpty()) {
            List<Farmer> requestFarmers = farmerRepository.findAllById(farmerIds);
            List<Farmer> farmersOwningTheFarm = farm.getFarmers();

            List<Farmer> newFarmOwners = Stream.concat(requestFarmers.stream(), farmersOwningTheFarm.stream())
                    .distinct().collect(Collectors.toList());
            List<Farmer> farmersObj = newFarmOwners.stream().filter(farmer -> farmer.getDeletedFlag() == Constants.NO)
                    .collect(Collectors.toList());

            farm.getFarmers().clear();
            farm.getFarmers().addAll(farmersObj);
        }
    }

    @Transactional
    public ApiResponse<?> addFarm(FarmRequest farmRequest) {
        try {

            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {

                log.info("Current admin: {}", currentUser);
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            List<Long> farmersIds = farmRequest.getFarmersIds();

            if (farmersIds != null && !farmersIds.isEmpty()) {
                List<Farmer> farmers = farmerRepository.findAllById(farmersIds);

                System.out.println("Number of Farmers found: " + farmers.size());

                if (farmers != null && !farmers.isEmpty()) {

                    List<Farmer> filteredFarmers = farmers.stream()
                            .filter(farmer -> farmer.getDeletedFlag() == Constants.NO)
                            .collect(Collectors.toList());
                    var farm = Farm.builder()
                            .typeOfOwnership(farmRequest.getTypeOfOwnership())
                            .farmers(filteredFarmers)
                            .titleNumber(farmRequest.getTitleNumber())
                            .titleDeed(farmRequest.getTitleDeed())
                            .farmSize(farmRequest.getFarmSize())
                            .latitude(farmRequest.getLatitude())
                            .longitude(farmRequest.getLongitude())
                            .farmIncome(farmRequest.getFarmIncome())
                            .sourceOfWater(farmRequest.getSourceOfWater())
                            .farmHistory(farmRequest.getFarmHistory())
                            .soilType(farmRequest.getSoilType())
                            .build();

                    var savedFarm = farmRepository.save(farm);

                    FarmResponse farmResponse = modelMapper.map(savedFarm, FarmResponse.class);

                    return new ApiResponse<FarmResponse>("Farm was successfully added.", farmResponse,
                            HttpStatus.OK.value());
                } else {
                    return new ApiResponse<>("Farmer not found", null,
                            HttpStatus.BAD_REQUEST.value());
                }
            } else {
                return new ApiResponse<>("Farmer is required.", null, HttpStatus.BAD_REQUEST.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while adding farm.", e);

            return new ApiResponse<>("An error occurred while adding farm.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> removeFarmerFromFarm(FarmRequest farmRequest, Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {

                log.info("Current admin: {}", currentUser);
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Farm> farmOptional = farmRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (farmOptional.isPresent()) {
                Farm farm = farmOptional.get();

                List<Long> farmerIds = farmRequest.getFarmersIds();

                if (farmerIds != null && !farmerIds.isEmpty()) {
                    List<Farmer> farmsToRemove = farm.getFarmers().stream()
                            .filter(farmer -> farmerIds.contains(farmer.getId())).collect(Collectors.toList());

                    farm.getFarmers().removeAll(farmsToRemove);

                }

                Farm updatedFarm = farmRepository.save(farm);
                FarmResponse farmResponse = modelMapper.map(updatedFarm, FarmResponse.class);

                return new ApiResponse<>("Farm was successfully updated", farmResponse, HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Farm not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while removing farmer from farm.", e);

            return new ApiResponse<>("An error occurred while removing farmer from farm.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    @Transactional
    public ApiResponse<?> farmDelete(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {

                log.info("Current admin: {}", currentUser);
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Farm> farmOptional = farmRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (farmOptional.isPresent()) {
                Farm farm = farmOptional.get();

                farm.setDeletedFlag(Constants.YES);
                farm.setDeletedBy(currentUser);
                farm.setDeletedAt(LocalDateTime.now());

                Farm deletedFarm = farmRepository.save(farm);

                return new ApiResponse<>("Farm was successfully deleted.", null, HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Farm not found", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting farm.", e);
            return new ApiResponse<>("An error occurred while deleting farm.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

}
