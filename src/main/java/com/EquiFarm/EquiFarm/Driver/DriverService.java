package com.EquiFarm.EquiFarm.Driver;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.EquiFarm.EquiFarm.Farmer.DTO.FarmerUserRequest;
import com.EquiFarm.EquiFarm.Farmer.DTO.FarmersResponse;
import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Order.DTO.OrderResponse;
import com.EquiFarm.EquiFarm.Order.Order;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.ServiceProviderUserResponse;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.ValueChain.ValueChainRepo;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.Driver.DTO.DriverResponse;
import com.EquiFarm.EquiFarm.Driver.DTO.DriverUserRequest;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ValueChainRepo valueChainRepo;

    @Transactional
    public ApiResponse<?> getDriverProfile() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Driver> driverOptional = driverRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (driverOptional.isPresent()) {
                Driver driver = driverOptional.get();

                DriverResponse driverResponse = modelMapper.map(driver, DriverResponse.class);

                return new ApiResponse<>("User Profile fetched successfully.", driverResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Driver profile not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }
        } catch (UsernameNotFoundException e){
            log.info("User not found", e);
            return new ApiResponse<>("User not Found", null,HttpStatus.NOT_FOUND.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching driver profile.", e);
            return new ApiResponse<>("An error occurred while fetching driver profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> driverProfileUpdate(DriverUserRequest driverUserRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                log.info("Current User: {}", currentUser);
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Driver> driverOptional = driverRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (driverOptional.isPresent()) {
                Driver driver = driverOptional.get();
                User user = driver.getUser();

                if (driverUserRequest.getIdNumber() != null
                        && driverUserRequest.getIdNumber().length() > 0
                        && !Objects.equals(driverUserRequest.getIdNumber(),
                        driver.getIdNumber())) {
                    Optional<Driver> driverIdOptional = driverRepository
                            .findByIdNumber(driverUserRequest.getIdNumber());
                    if (driverIdOptional.isPresent()) {
                        return new ApiResponse<>("User with this Id Number already exists.",
                                null,
                                HttpStatus.NOT_FOUND.value());
                    }
                    driver.setIdNumber(driverUserRequest.getIdNumber());
                }

                if (driverUserRequest.getValueChainIds() != null) {
                    if (driverUserRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    // replace existing value chain
                    Set<Long> newValueChainIds = new HashSet<>(driverUserRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = driver.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getDriverList().remove(driver);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    // add new value chain
                    for (Long valueId : driverUserRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!driver.getValueChains().contains(valueChain)) {
                            driver.getValueChains().add(valueChain);
                            valueChain.getDriverList().add(driver);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }

                updateDriverFields(driverUserRequest, driver, user);

                userRepository.save(user);
                Driver updatedDriver = driverRepository.save(driver);

                DriverResponse driverResponse = modelMapper.map(updatedDriver, DriverResponse.class);

                return new ApiResponse<>("Driver profile was successfully updated.", driverResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Driver not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating the driver profile.", e);
            return new ApiResponse<>("An error occurred while updating the driver profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void updateDriverFields(DriverUserRequest driverUserRequest, Driver driver, User user) {
//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getFirstName(),
//                user::setFirstName,
//                user::getFirstName);
//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getLastName(),
//                user::setLastName,
//                user::getLastName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getLatitude(),
                driver::setLatitude,
                driver::getLatitude);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getLongitude(),
                driver::setLongitude,
                driver::getLongitude);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getGender(),
                driver::setGender,
                driver::getGender);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.isAvailable(),
                driver::setAvailable,
                driver::isAvailable);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getProfilePicture(),
                driver::setProfilePicture,
                driver::getProfilePicture);

        FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getBio(),
                driver::setBio,
                driver::getBio);
    }

    @Transactional
    public ApiResponse<?> updateDriver(DriverUserRequest driverUserRequest, Long id) {
        try {
            Optional<Driver> driverOptional = driverRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (driverOptional.isPresent()) {
                Driver driver = driverOptional.get();
                User user = driver.getUser();

                if (driverUserRequest.getIdNumber() != null
                        && driverUserRequest.getIdNumber().length() > 0
                        && !Objects.equals(driverUserRequest.getIdNumber(),
                        driver.getIdNumber())) {
                    Optional<Driver> driverIdOptional = driverRepository
                            .findByIdNumber(driverUserRequest.getIdNumber());
                    if (driverIdOptional.isPresent()) {
                        return new ApiResponse<>("User with this Id Number already exists.",
                                null,
                                HttpStatus.NOT_FOUND.value());
                    }
                    driver.setIdNumber(driverUserRequest.getIdNumber());
                }
                if (driverUserRequest.getValueChainIds() != null) {
                    if (driverUserRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    // replace existing value chain
                    Set<Long> newValueChainIds = new HashSet<>(driverUserRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = driver.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getDriverList().remove(driver);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : driverUserRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!driver.getValueChains().contains(valueChain)) {
                            driver.getValueChains().add(valueChain);
                            valueChain.getDriverList().add(driver);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }

                updateDriverFields(driverUserRequest, driver, user);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getEmail(),
                        user::setEmail,
                        user::getEmail);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getPhoneNo(),
                        user::setPhoneNo,
                        user::getPhoneNo);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getModeOfTransport(),
                        driver::setModeOfTransport,
                        driver::getModeOfTransport);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getNumberPlate(),
                        driver::setNumberPlate,
                        driver::getNumberPlate);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getVehicleImage(),
                        driver::setVehicleImage,
                        driver::getVehicleImage);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(driverUserRequest.getLicenceNumber(),
                        driver::setLicenceNumber,
                        driver::getLicenceNumber);

                userRepository.save(user);
                Driver updatedDriver = driverRepository.save(driver);

                DriverResponse driverResponse = modelMapper.map(updatedDriver, DriverResponse.class);

                return new ApiResponse<>("Driver profile was successfully updated.", driverResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Driver not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while updating the driver profile.", e);
            return new ApiResponse<>("An error occurred while updating the driver profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> updateDriverByUserId(DriverUserRequest driverUserRequest, Long userId) {
        try {

            Optional<Driver> driverOptional = driverRepository.findByDeletedFlagAndUserId(Constants.NO, userId);

            if (driverOptional.isPresent()) {
                Driver driver = driverOptional.get();
                User user = driver.getUser();

                updateDriverFields(driverUserRequest, driver, user);
                userRepository.save(user);

                if (driverUserRequest.getValueChainIds() != null) {
                    if (driverUserRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    // replace existing value chain
                    Set<Long> newValueChainIds = new HashSet<>(driverUserRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = driver.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getDriverList().remove(driver);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : driverUserRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!driver.getValueChains().contains(valueChain)) {
                            driver.getValueChains().add(valueChain);
                            valueChain.getDriverList().add(driver);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }

                Driver updatedDriver = driverRepository.save(driver);
                DriverResponse driverResponse = modelMapper.map(updatedDriver, DriverResponse.class);

                return new ApiResponse<DriverResponse>("Driver profile updated successfully.",
                        driverResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Driver not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating the driver profile.", e);
            return new ApiResponse<>("An error occurred while updating the driver profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getDriverByDriverId(Long id) {
        try {
            Optional<Driver> driverOptional = driverRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (driverOptional.isPresent()) {
                Driver driver = driverOptional.get();

                DriverResponse driverResponse = modelMapper.map(driver, DriverResponse.class);

                return new ApiResponse<>("Driver fetched successfully.", driverResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Driver not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching driver.", e);

            return new ApiResponse<>("An error occurred while fetching driver.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAllDrivers() {
        try {
            List<Driver> driverList = driverRepository.findByDeletedFlag(Constants.NO);

            List<DriverResponse> driverResponses = driverList.stream()
                    .map(driver -> modelMapper.map(driver, DriverResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Driver fetched successfully.", driverResponses,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching Drivers.", e);

            return new ApiResponse<>("An error occurred while fetching Drivers.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllDriversInValueChain(Long valueChainId) {
        try {
            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueChainId, Constants.NO);
            if (valueChainOptional.isEmpty()) {
                return new ApiResponse<>("Value chain not found", null, HttpStatus.NOT_FOUND.value());
            }
            ValueChain valueChain = valueChainOptional.get();
            List<Driver> driverList = driverRepository.findByValueChainsContaining(valueChain);
            List<DriverResponse> driverResponses = driverList
                    .stream()
                    .map(driver -> modelMapper
                            .map(driver, DriverResponse.class))
                    .collect(Collectors.toList());
            return new ApiResponse<>("Drivers fetched successfully", driverResponses, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error occurred: ", e);
            return new ApiResponse<>("Error occurred fetching Drivers", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getDriverByUserId(Long userId) {
        try {
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, userId);
            if (userOptional.isPresent()) {
                Optional<Driver> driverList = driverRepository.findByDeletedFlagAndUserId(Constants.NO, userId);

                List<DriverResponse> driverResponses = driverList.stream()
                        .map(order -> modelMapper.map(order, DriverResponse.class))
                        .collect(Collectors.toList());


                return new ApiResponse<>("Drivers Fetched successfully.", driverResponses, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("User not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching user orders.", e);

            return new ApiResponse<>("An error occurred while fetching user orders.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    @Transactional
    public ApiResponse<?> driverDelete(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<Driver> driverOptional = driverRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (driverOptional.isPresent()) {
                Driver driver = driverOptional.get();

                driver.setDeletedAt(LocalDateTime.now());
                driver.setDeletedBy(currentUser);
                driver.setDeletedFlag(Constants.YES);

                User user = driver.getUser();
                user.setDeletedAt(LocalDateTime.now());
                user.setDeletedBy(currentUser);
                user.setDeletedFlag(Constants.YES);

                user.setActive(false);

                userRepository.save(user);
                driverRepository.save(driver);

                return new ApiResponse<>("Driver was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Driver not found.", null, HttpStatus.NOT_FOUND.value());

            }

        } catch (Exception e) {
            log.error("An error occurred while deleting driver.", e);

            return new ApiResponse<>("An error occurred while deleting driver.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
