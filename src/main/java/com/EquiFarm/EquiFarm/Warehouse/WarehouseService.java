package com.EquiFarm.EquiFarm.Warehouse;


import com.EquiFarm.EquiFarm.Manufacturer.DTO.ManufacturerResponse;
import com.EquiFarm.EquiFarm.Manufacturer.Manufacturer;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.ValueChain.ValueChainRepo;
import com.EquiFarm.EquiFarm.Warehouse.DTO.WarehouseRequest;
import com.EquiFarm.EquiFarm.Warehouse.DTO.WarehouseResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
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

@Slf4j
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepo warehouseRepo;
    private final ModelMapper modelMapper;
    private  final UserRepository userRepository;
    private final ValueChainRepo valueChainRepo;

    public ApiResponse<?> fetchWarehouseProfile() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }


            Optional<Warehouse> warehouseOptional = warehouseRepo.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());
            if (warehouseOptional.isPresent()) {
                Warehouse warehouse = warehouseOptional.get();
                WarehouseResponse warehouseResponse = modelMapper.map(warehouse, WarehouseResponse.class);
                return new ApiResponse<>("Success fetching Warehouse's Profile", warehouseResponse, HttpStatus.FOUND.value());

            } else {
                return new ApiResponse<>("Warehouse Profile not found", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.info("Error fetching Warehouse's Profile", e);
            return new ApiResponse<>("Error fetching Warehouse's Profile", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getWarehouseByUserId(Long id) {
        try {
            Optional<Warehouse> warehouseOptional = warehouseRepo.findByDeletedFlagAndUserId(Constants.NO, id);
            if (warehouseOptional.isPresent()) {
                Warehouse warehouse = warehouseOptional.get();


                System.out.println("Warehouse Found: " + warehouse);

                WarehouseResponse warehouseUserResponse = modelMapper.map(warehouse,
                        WarehouseResponse.class);

                return new ApiResponse<>("Warehouse fetched successfully", warehouseUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Warehouse not found.", null, HttpStatus.NOT_FOUND.value());

            }
        } catch (Exception e) {
            log.error("An error occurred while fetching warehouse.", e);


            return new ApiResponse<>("An error occurred while fetching Warehouse.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getALlWarehouses() {
        try {
            List<Warehouse> warehouseList = warehouseRepo.findByDeletedFlag(Constants.NO);

            List<WarehouseResponse> warehouseResponse = warehouseList.stream()
                    .map(warehouse -> modelMapper.map(warehouse, WarehouseResponse.class))
                    .toList();

            return new ApiResponse<>("Warehouses fetched successfully", warehouseResponse,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error Occurred Fetching all Warehouses", e);
            return new ApiResponse<>("Error occurred fetching all warehouses", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllWarehousesInValueChain(Long valueChainId) {
        try {
            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueChainId, Constants.NO);
            if (valueChainOptional.isEmpty()) {
                return new ApiResponse<>("Value chain not found", null, HttpStatus.NOT_FOUND.value());
            }
            ValueChain valueChain = valueChainOptional.get();
            List<Warehouse> warehouseList = warehouseRepo.findByValueChainsContaining(valueChain);
            List<WarehouseResponse> warehouseResponses = warehouseList
                    .stream()
                    .map(warehouse -> modelMapper
                            .map(warehouse, WarehouseResponse.class))
                    .toList();
            return new ApiResponse<>("Drivers fetched successfully", warehouseResponses, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error occurred: ", e);
            return new ApiResponse<>("Error occurred fetching Drivers", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getVacantWarehouses() {
        try {
            List<Warehouse> warehouseList = warehouseRepo.findByVacant(true);
            List<WarehouseResponse> vacantWarehouses = warehouseList.stream()
                    .map(warehouse -> modelMapper.map(warehouse, WarehouseResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Vacant warehouses fetched successfully.", vacantWarehouses, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching vacant warehouses.", e);

            return new ApiResponse<>("An error occurred while fetching vacant warehouses.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> getWarehouseByCapacity(Double capacity) {
        try {
            List<Warehouse> warehouseList = warehouseRepo.findByCapacity(capacity);
            List<WarehouseResponse> warehouseCapacity = warehouseList.stream()
                    .map(warehouse -> modelMapper.map(warehouse, WarehouseResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Warehouse Capacity fetched successfully.", warehouseCapacity, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching warehouse capacity.", e);

            return new ApiResponse<>("An error occurred while fetching warehouse capacity.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> warehouseProfileUpdate(WarehouseRequest warehouseRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Warehouse> warehouseOptional = warehouseRepo
                    .findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());

            if (warehouseOptional.isPresent()) {
                Warehouse warehouse = warehouseOptional.get();

                if (warehouseRequest.getIdNumber() != null
                        && warehouseRequest.getIdNumber().length() > 0
                        && !Objects.equals(warehouseRequest.getIdNumber(), warehouse.getIdNumber())) {
                    Optional<Warehouse> warehouseIdOptional = warehouseRepo
                            .findByIdNumber(warehouseRequest.getIdNumber());
                    if (warehouseIdOptional.isPresent()) {
                        return new ApiResponse<>("User with this Id Number already exists.", null, HttpStatus.FORBIDDEN.value());
                    }
                    warehouse.setIdNumber(warehouseRequest.getIdNumber());
                }

                if (warehouseRequest.getValueChainIds() != null) {
                    if (warehouseRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    // replace existing value chain
                    Set<Long> newValueChainIds = new HashSet<>(warehouseRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = warehouse.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getWarehouseList().remove(warehouse);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : warehouseRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!warehouse.getValueChains().contains(valueChain)) {
                            warehouse.getValueChains().add(valueChain);
                            valueChain.getWarehouseList().add(warehouse);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }
                warehouse.setWarehouseVerified(false);
                updateWarehouseFields(warehouse, warehouseRequest);
                updateBusinessLocation(warehouse, warehouseRequest);

                Warehouse updatedWarehouse = warehouseRepo.save(warehouse);

                WarehouseResponse warehouseResponse = modelMapper.map(updatedWarehouse,
                        WarehouseResponse.class);

                return new ApiResponse<>("Warehouse profile was successfully updated.",
                        warehouseResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Category not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating warehouse's profile.", e);
            return new ApiResponse<>("An error occurred while updating warehouse's profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void updateWarehouseFields(Warehouse warehouse,
                                          WarehouseRequest warehouseRequest) {
//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getUser().getFirstName(),
//                warehouse.getUser()::setFirstName, warehouse.getUser()::getFirstName);
//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getUser().getLastName(),
//                warehouse.getUser()::setLastName, warehouse.getUser()::getLastName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getProfilePicture(),
                warehouse::setProfilePicture, warehouse::getProfilePicture);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getGender(),
                warehouse::setGender, warehouse::getGender);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getBio(), warehouse::setBio,
                warehouse::getBio);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getGender(),
                warehouse::setGender, warehouse::getGender);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getWarehouseName(),
                warehouse::setWarehouseName, warehouse::getWarehouseName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getWarehouseEmail(),
                warehouse::setWarehouseEmail, warehouse::getWarehouseEmail);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getWarehouseDescription(),
                warehouse::setWarehouseDescription, warehouse::getWarehouseDescription);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getWarehouseLogo(),
                warehouse::setWarehouseLogo, warehouse::getWarehouseLogo);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getLicenceNumber(),
                warehouse::setLicenceNumber, warehouse::getLicenceNumber);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getWarehousePhoneNumber(),
                warehouse::setWarehousePhoneNumber, warehouse::getWarehousePhoneNumber);
    }

    private void updateBusinessLocation(Warehouse warehouse,
                                        WarehouseRequest warehouseRequest) {
        if (warehouseRequest.getBusinessLocation() != null) {
            Cordinates coordinates = warehouse.getWarehouseLocation();
            if (coordinates == null) {
                coordinates = new Cordinates();
            }
            coordinates.setLatitude(warehouseRequest.getBusinessLocation().getLatitude());
            coordinates.setLongitude(warehouseRequest.getBusinessLocation().getLongitude());
            warehouse.setWarehouseLocation(coordinates);
        }
    }

    public ApiResponse<?> updateWarehouseByWarehouseId(WarehouseRequest warehouseRequest, Long warehouseId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. Unauthenticated User", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<Warehouse> warehouseOptional = warehouseRepo.findByIdAndDeletedFlag(warehouseId, Constants.NO);
            if (warehouseOptional.isPresent()) {
                Warehouse warehouse = warehouseOptional.get();
                if (warehouseRequest.getIdNumber() != null
                        && warehouseRequest.getIdNumber().length() > 0
                        && !Objects.equals(warehouseRequest.getIdNumber(), warehouse.getIdNumber())) {
                    Optional<Warehouse> warehouseIdOptional = warehouseRepo
                            .findByIdNumber(warehouseRequest.getIdNumber());
                    if (warehouseIdOptional.isPresent()) {
                        return new ApiResponse<>("Id Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    warehouse.setIdNumber(warehouseRequest.getIdNumber());
                }

                if (warehouseRequest.getUser().getPhoneNo() != null
                        && warehouseRequest.getUser().getPhoneNo().length() > 0
                        && !Objects.equals(warehouseRequest.getUser().getPhoneNo(), warehouse.getUser().getPhoneNo())) {
                    Optional<User> userOptional = userRepository.findUserByPhoneNo(warehouseRequest.getUser().getPhoneNo());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Phone Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    warehouse.getUser().setPhoneNo(warehouseRequest.getUser().getPhoneNo());
                }

                if (warehouseRequest.getUser().getEmail() != null
                        && warehouseRequest.getUser().getEmail().length() > 0
                        && !Objects.equals(warehouseRequest.getUser().getEmail(), warehouse.getUser().getEmail())) {
                    Optional<User> userOptional = userRepository.findByEmail(warehouseRequest.getUser().getEmail());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Email already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    warehouse.getUser().setEmail(warehouseRequest.getUser().getEmail());
                }

                if (warehouseRequest.getValueChainIds() != null) {
                    if (warehouseRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    // replace existing value chain
                    Set<Long> newValueChainIds = new HashSet<>(warehouseRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = warehouse.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getWarehouseList().remove(warehouse);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : warehouseRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!warehouse.getValueChains().contains(valueChain)) {
                            warehouse.getValueChains().add(valueChain);
                            valueChain.getWarehouseList().add(warehouse);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }

                updateWarehouseFields(warehouse, warehouseRequest);
                updateBusinessLocation(warehouse, warehouseRequest);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getUser().getEmail(),
                        warehouse.getUser()::setEmail, warehouse.getUser()::getEmail);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getUser().getPhoneNo(),
                        warehouse.getUser()::setPhoneNo, warehouse.getUser()::getPhoneNo);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getWarehousePhoneNumber(),
                        warehouse::setWarehousePhoneNumber, warehouse::getWarehousePhoneNumber);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getWarehouseVerified(),
                        warehouse::setWarehouseVerified, warehouse::getWarehouseVerified);

                Warehouse updatedWarehouse = warehouseRepo.save(warehouse);

                WarehouseResponse warehouseResponse = modelMapper.map(updatedWarehouse,
                        WarehouseResponse.class);

                return new ApiResponse<>("Warehouse profile successfully updated.",
                        warehouseResponse, HttpStatus.OK.value());
            }
            return new ApiResponse<>("Warehouse not found", null, HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            log.info("Error Updating Warehouse by User id", e);
            return new ApiResponse<>("Error Updating Warehouse by User ID", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    public ApiResponse<?> updateWarehouseByUserId(WarehouseRequest warehouseRequest, Long userId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. Unauthenticated User", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<Warehouse> warehouseOptional = warehouseRepo.findByDeletedFlagAndUserId(Constants.NO, userId);
            if (warehouseOptional.isPresent()) {
                Warehouse warehouse = warehouseOptional.get();
                if (warehouseRequest.getIdNumber() != null
                        && warehouseRequest.getIdNumber().length() > 0
                        && !Objects.equals(warehouseRequest.getIdNumber(), warehouse.getIdNumber())) {
                    Optional<Warehouse> warehouseIdOptional = warehouseRepo
                            .findByIdNumber(warehouseRequest.getIdNumber());
                    if (warehouseIdOptional.isPresent()) {
                        return new ApiResponse<>("Id Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    warehouse.setIdNumber(warehouseRequest.getIdNumber());
                }

                if (warehouseRequest.getUser().getPhoneNo() != null
                        && warehouseRequest.getUser().getPhoneNo().length() > 0
                        && !Objects.equals(warehouseRequest.getUser().getPhoneNo(), warehouse.getUser().getPhoneNo())) {
                    Optional<User> userOptional = userRepository.findUserByPhoneNo(warehouseRequest.getUser().getPhoneNo());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Phone Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    warehouse.getUser().setPhoneNo(warehouseRequest.getUser().getPhoneNo());
                }

                if (warehouseRequest.getUser().getEmail() != null
                        && warehouseRequest.getUser().getEmail().length() > 0
                        && !Objects.equals(warehouseRequest.getUser().getEmail(), warehouse.getUser().getEmail())) {
                    Optional<User> userOptional = userRepository.findByEmail(warehouseRequest.getUser().getEmail());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Email already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    warehouse.getUser().setEmail(warehouseRequest.getUser().getEmail());
                }

                if (warehouseRequest.getValueChainIds() != null) {
                    if (warehouseRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    // replace existing value chain
                    Set<Long> newValueChainIds = new HashSet<>(warehouseRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = warehouse.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getWarehouseList().remove(warehouse);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : warehouseRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!warehouse.getValueChains().contains(valueChain)) {
                            warehouse.getValueChains().add(valueChain);
                            valueChain.getWarehouseList().add(warehouse);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }

                updateWarehouseFields(warehouse, warehouseRequest);
                updateBusinessLocation(warehouse, warehouseRequest);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getUser().getEmail(),
                        warehouse.getUser()::setEmail, warehouse.getUser()::getEmail);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getUser().getPhoneNo(),
                        warehouse.getUser()::setPhoneNo, warehouse.getUser()::getPhoneNo);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getWarehousePhoneNumber(),
                        warehouse::setWarehousePhoneNumber, warehouse::getWarehousePhoneNumber);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(warehouseRequest.getWarehouseVerified(),
                        warehouse::setWarehouseVerified, warehouse::getWarehouseVerified);

                Warehouse updatedWarehouse = warehouseRepo.save(warehouse);

                WarehouseResponse warehouseResponse = modelMapper.map(updatedWarehouse,
                        WarehouseResponse.class);

                return new ApiResponse<>("Warehouse profile successfully updated.",
                        warehouseResponse, HttpStatus.OK.value());
            }
            return new ApiResponse<>("Warehouse not found", null, HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            log.info("Error Updating Warehouse by User id", e);
            return new ApiResponse<>("Error Updating Warehouse by User ID", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    public ApiResponse<?> deleteWarehouse(Long warehouseId) {
        try {
            Optional<Warehouse> warehouseOptional = warehouseRepo.findByDeletedFlagAndUserId(Constants.NO, warehouseId);

            if (warehouseOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();
                Warehouse warehouse = warehouseOptional.get();
                warehouse.setDeletedFlag(Constants.YES);
                warehouse.setDeletedAt(LocalDateTime.now());
                warehouse.setAddedBy(currentUser);

                User user = warehouse.getUser();
                user.setDeletedAt(LocalDateTime.now());
                user.setDeletedBy(currentUser);
                user.setDeletedFlag(Constants.YES);
                user.setActive(false);

                userRepository.save(user);

                warehouseRepo.save(warehouse);


                return new ApiResponse<>("Warehouse was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Warehouse not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {

            log.error("An error occurred while deleting warehouse.", e);

            return new ApiResponse<>("An error occurred while deleting warehouse.", null,

                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}

