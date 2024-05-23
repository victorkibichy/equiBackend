package com.EquiFarm.EquiFarm.Farmer;

import com.EquiFarm.EquiFarm.Farmer.DTO.FarmerUserRequest;
import com.EquiFarm.EquiFarm.Farmer.DTO.FarmersResponse;
import com.EquiFarm.EquiFarm.Order.OrderRepository;
import com.EquiFarm.EquiFarm.Staff.Staff;
import com.EquiFarm.EquiFarm.Staff.StaffRepository;
import com.EquiFarm.EquiFarm.Staff.StaffRole;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.ValueChain.ValueChainRepo;
import com.EquiFarm.EquiFarm.Wallet.WalletService;
import com.EquiFarm.EquiFarm.auth.AuthenticationService;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.Role;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FarmerService {
    private final FarmerRepository farmerRepository;
    // private final TypeOfAgricultureRepository typeOfAgricultureRepository;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;
    private final ValueChainRepo valueChainRepo;
    private final OrderRepository orderRepository;
    private final StaffRepository staffRepository;
    private final AuthenticationService authenticationService;
    private final WalletService walletService;

    public ApiResponse<?> getFarmerProfile() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (farmerOptional.isPresent()) {
                log.info("Farmer Profile: {}", farmerOptional.get().getUser().getNationalId());

                Farmer farmer = farmerOptional.get();

                FarmersResponse farmersResponse = modelMapper.map(farmer, FarmersResponse.class);

                return new ApiResponse<FarmersResponse>("User profile fetched successfully",
                        farmersResponse,
                        HttpStatus.OK.value());
            } else {

                try {
                    authenticationService.createUserProfile(currentUser);
                    walletService.createWallet(currentUser);

                    Optional<Farmer> farmerOptional2 = farmerRepository.findByDeletedFlagAndUserId(Constants.NO,
                            currentUser.getId());
                    if (farmerOptional2.isPresent()) {
                        Farmer farmer = farmerOptional2.get();
                        FarmersResponse farmersResponse = modelMapper.map(farmer, FarmersResponse.class);

                        return new ApiResponse<FarmersResponse>("User profile created successfully",
                                farmersResponse,
                                HttpStatus.OK.value());
                    } else {
                        return new ApiResponse<>("Error creating user profile", null,
                                HttpStatus.INTERNAL_SERVER_ERROR.value());
                    }
                } catch (Exception e) {
                    return new ApiResponse<>("Error creating user profile", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching farmer profile.", e);
            return new ApiResponse<>("An error occurred while fetching farmer profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getFarmerByFarmerId(Long id) {
        try {
            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (farmerOptional.isPresent()) {
                Farmer farmer = farmerOptional.get();
                FarmersResponse farmersResponse = modelMapper.map(farmer, FarmersResponse.class);

                return new ApiResponse<FarmersResponse>("Farmer Fetched Successfully.", farmersResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Farmer not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching farmer", e);

            return new ApiResponse<>("An error occurred while fetching farmer.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getFarmerByUserId(Long userId) {
        try {
            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndUserId(Constants.NO, userId);
            if (farmerOptional.isPresent()) {
                Farmer farmer = farmerOptional.get();
                FarmersResponse farmersResponse = modelMapper.map(farmer, FarmersResponse.class);

                return new ApiResponse<FarmersResponse>("Farmer Fetched Successfully.", farmersResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Farmer not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching farmer", e);

            return new ApiResponse<>("An error occurred while fetching farmer.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllFarmers() {
        try {
            List<Farmer> farmerList = farmerRepository.findByDeletedFlag(Constants.NO);

            List<FarmersResponse> farmerResponseList = farmerList
                    .stream()
//                                .filter(farmer -> {
//                                        if (valueChainId == null) {
//                                                return true;  // Include all farmers if valueChainId is not provided
//                                        }
//                                        // Check if any of the farmer's value chains match the given valueChainId
//                                        return farmer.getValueChains().stream().anyMatch(vc -> vc.getId().equals(valueChainId));
//                                })
                    .map(farmer -> modelMapper.map(farmer, FarmersResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Farmers fetched successfully.", farmerResponseList, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching Farmers.", e);
            return new ApiResponse<>("An error occurred while fetching Farmers.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllFarmersInValueChain(Long valueChainId) {
        try {
            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueChainId, Constants.NO);
            if (valueChainOptional.isEmpty()) {
                return new ApiResponse<>("Value chain not found", null, HttpStatus.NOT_FOUND.value());
            }
            ValueChain valueChain = valueChainOptional.get();
            List<Farmer> farmerList = farmerRepository.findByValueChainsContaining(valueChain);
            List<FarmersResponse> farmersResponses = farmerList
                    .stream()
                    .map(farmer -> modelMapper
                            .map(farmer, FarmersResponse.class))
                    .toList();
            return new ApiResponse<>("Farmers fetched successfully", farmersResponses, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error occurred: ", e);
            return new ApiResponse<>("Error occurred fetching Farmers", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> farmerProfileUpdate(FarmerUserRequest farmerUserRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                log.info("Current User: {}", currentUser);
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (farmerOptional.isPresent()) {
                Farmer farmer = farmerOptional.get();
                User user = farmer.getUser();

                updateFarmerFields(farmer, farmerUserRequest);
                userRepository.save(user);
//                                if (farmerUserRequest.getValueChainId() != null){
//                                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(farmerUserRequest.getValueChainId(), Constants.NO);
//                                        if(valueChainOptional.isEmpty()){
//                                                return new ApiResponse<>("Value Chain Not Found", null,HttpStatus.NOT_FOUND.value());
//                                        }
//                                        ValueChain valueChain = valueChainOptional.get();
//                                        farmer.setValueChain(valueChain);
//                                }
                if (farmerUserRequest.getValueChainIds() != null) {
                    if (farmerUserRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    Set<Long> newValueChainIds = new HashSet<>(farmerUserRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = farmer.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getFarmerList().remove(farmer);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : farmerUserRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!farmer.getValueChains().contains(valueChain)) {
                            farmer.getValueChains().add(valueChain);
                            valueChain.getFarmerList().add(farmer);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }
                Farmer updatedFarmer = farmerRepository.save(farmer);
                FarmersResponse farmerResponse = modelMapper.map(updatedFarmer, FarmersResponse.class);

                return new ApiResponse<FarmersResponse>("Farmer profile updated successfully.",
                        farmerResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Farmer not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating the farmer profile.", e);
            return new ApiResponse<>("An error occurred while updating the farmer profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void updateFarmerFields(Farmer farmer, FarmerUserRequest farmerUserRequest) {

//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmerUserRequest.getFirstName(),
//                farmer.getUser()::setFirstName,
//                farmer.getUser()::getFirstName);
//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmerUserRequest.getLastName(),
//                farmer.getUser()::setLastName,
//                farmer.getUser()::getLastName);
//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmerUserRequest.getIdNumber(),
//                farmer::setIdNumber,
//                farmer::getIdNumber);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmerUserRequest.getEmail(),
                farmer.getUser()::setEmail,
                farmer.getUser()::getEmail);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmerUserRequest.getPhoneNo(),
                farmer.getUser()::setPhoneNo,
                farmer.getUser()::getPhoneNo);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmerUserRequest.getBio(),
                farmer::setBio, farmer::getBio);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmerUserRequest.getProfilePicture(),
                farmer::setProfilePicture, farmer::getProfilePicture);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmerUserRequest.getLatitude(),
                farmer::setLatitude,
                farmer::getLatitude);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmerUserRequest.getLongitude(),
                farmer::setLongitude,
                farmer::getLongitude);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmerUserRequest.getGender(),
                farmer::setGender,
                farmer::getGender);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmerUserRequest.getTypeOfFarming(),
                farmer::setTypeOfFarming,
                farmer::getTypeOfFarming);

        //Update FIELD_OFFICER

    }

    public ApiResponse<?> updateFarmerProfile(FarmerUserRequest farmerUserRequest, Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            } else {
                Optional<Staff> staffOptional = staffRepository.findByDeletedFlagAndUserId(
                        Constants.NO, currentUser.getId());
                if (staffOptional.isEmpty() && currentUser.getRole() != Role.ADMIN){
                    return new ApiResponse<>("Action unauthorized.", null,
                            HttpStatus.UNAUTHORIZED.value());
                }
                if (staffOptional.isPresent()) {
                    Staff staff = staffOptional.get();
                    if (staff.getStaffRole() == StaffRole.UNCLASSIFIED_STAFF) {
                        return new ApiResponse<>("Action unauthorized.", null,
                                HttpStatus.UNAUTHORIZED.value());
                    }
                }

                Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndId(Constants.NO, id);

                if (farmerOptional.isPresent()) {
                    Farmer farmer = farmerOptional.get();

                    // Update farmer fields
                    updateFarmerFields(farmer, farmerUserRequest);

                    if (farmerUserRequest.getValueChainIds() != null) {
                        if (farmerUserRequest.getValueChainIds().size() == 0) {
                            return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                        }
                        Set<Long> newValueChainIds = new HashSet<>(farmerUserRequest.getValueChainIds());

                        Iterator<ValueChain> iterator = farmer.getValueChains().iterator();
                        while (iterator.hasNext()) {
                            ValueChain existingValueChain = iterator.next();
                            if (!newValueChainIds.contains(existingValueChain.getId())) {
                                iterator.remove();
                                existingValueChain.getFarmerList().remove(farmer);
                                valueChainRepo.save(existingValueChain);
                            }
                        }
                        for (Long valueId : farmerUserRequest.getValueChainIds()) {
                            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                            if (valueChainOptional.isEmpty()) {
                                return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                            }
                            ValueChain valueChain = valueChainOptional.get();
                            if (!farmer.getValueChains().contains(valueChain)) {
                                farmer.getValueChains().add(valueChain);
                                valueChain.getFarmerList().add(farmer);
                                valueChainRepo.save(valueChain);
                            }
                        }
                    }


                    if (farmerUserRequest.getFieldOfficerId() != null) {
                        Optional<Staff> staffOptional2 = staffRepository
                                .findByDeletedFlagAndId(Constants.NO, farmerUserRequest.getFieldOfficerId());

                        if (staffOptional2.isPresent()) {
                            Staff fieldOfficer = staffOptional2.get();
                            if (fieldOfficer.getStaffRole() == StaffRole.FIELD_OFFICER) {
                                farmer.setFieldOfficer(fieldOfficer);
                            }
                            else {
                                return new ApiResponse<>("Staff is not a field officer.", null,
                                        HttpStatus.BAD_REQUEST.value());
                            }

                        } else {
                            return new ApiResponse<>("Field officer not found.", null,
                                    HttpStatus.BAD_REQUEST.value());
                        }
                    }

                    // Save the updated farmer
                    Farmer updatedFarmer = farmerRepository.save(farmer);
                    FarmersResponse farmerResponse = modelMapper.map(updatedFarmer, FarmersResponse.class);

                    return new ApiResponse<>("Farmer profile updated successfully.", farmerResponse,
                            HttpStatus.OK.value());
                } else {
                    return new ApiResponse<>("Farmer not found", null, HttpStatus.NOT_FOUND.value());
                }
            }





        } catch (Exception e) {
            log.error("An error occurred while updating the farmer profile.", e);
            return new ApiResponse<>("An error occurred while updating the farmer profile.", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> updateFarmerByUserId(FarmerUserRequest farmerUserRequest, Long userId) {
        try {

            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndUserId(Constants.NO, userId);

            if (farmerOptional.isPresent()) {
                Farmer farmer = farmerOptional.get();
                User user = farmer.getUser();

                updateFarmerFields(farmer, farmerUserRequest);
                userRepository.save(user);

//                                if (farmerUserRequest.getValueChainId() != null){
//                                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(farmerUserRequest.getValueChainId(), Constants.NO);
//                                        if(valueChainOptional.isEmpty()){
//                                                return new ApiResponse<>("Value Chain Not Found", null,HttpStatus.NOT_FOUND.value());
//                                        }
//                                        ValueChain valueChain = valueChainOptional.get();
//                                        farmer.setValueChain(valueChain);
//                                }
                if (farmerUserRequest.getValueChainIds() != null) {
                    if (farmerUserRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    Set<Long> newValueChainIds = new HashSet<>(farmerUserRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = farmer.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getFarmerList().remove(farmer);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : farmerUserRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!farmer.getValueChains().contains(valueChain)) {
                            farmer.getValueChains().add(valueChain);
                            valueChain.getFarmerList().add(farmer);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }

                Farmer updatedFarmer = farmerRepository.save(farmer);
                FarmersResponse farmerResponse = modelMapper.map(updatedFarmer, FarmersResponse.class);

                return new ApiResponse<FarmersResponse>("Farmer profile updated successfully.",
                        farmerResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Farmer not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating the farmer profile.", e);
            return new ApiResponse<>("An error occurred while updating the farmer profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> farmerDelete(Long id) {
        try {
            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (farmerOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();

                Farmer farmer = farmerOptional.get();
                farmer.setDeletedFlag(Constants.YES);
                farmer.setDeletedAt(LocalDateTime.now());
                farmer.setDeletedBy(currentUser);

                User user = farmer.getUser();
                user.setDeletedFlag(Constants.YES);
                user.setDeletedBy(currentUser);
                user.setDeletedAt(LocalDateTime.now());

                user.setActive(false);
                userRepository.save(user);
                farmerRepository.save(farmer);

                return new ApiResponse<>("Farmer was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Farmer not found.", null, HttpStatus.NOT_FOUND.value());

            }
        } catch (Exception e) {
            log.error("An error occurred while deleting farmer", e);

            return new ApiResponse<>("An error occurred while deleting farmer", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
