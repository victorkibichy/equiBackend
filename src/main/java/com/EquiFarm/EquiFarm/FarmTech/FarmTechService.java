package com.EquiFarm.EquiFarm.FarmTech;

import com.EquiFarm.EquiFarm.FarmTech.DTO.FarmTechRequest;
import com.EquiFarm.EquiFarm.FarmTech.DTO.FarmTechResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;
import com.EquiFarm.EquiFarm.ServiceProvider.WorkingHours;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.ValueChain.ValueChainRepo;
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
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FarmTechService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final FarmTechRepository farmTechRepository;
    private final ValueChainRepo valueChainRepo;

    public ApiResponse<?> getFarmTechOwnerProfile() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<FarmTech> farmTechOptional = farmTechRepository
                    .findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());

            if (farmTechOptional.isPresent()) {
                FarmTech farmTech = farmTechOptional.get();

                FarmTechResponse farmTechResponse = modelMapper.map(farmTech, FarmTechResponse.class);

                return new ApiResponse<>("Farmtech fetched successfully.", farmTechResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Farmtech not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching farmtech.", e);
            return new ApiResponse<>("An error occurred while fetching farmtech.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    public ApiResponse<?> getFarmTechOwnerById(Long id) {
        try {
            Optional<FarmTech> farmTechOptional = farmTechRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (farmTechOptional.isPresent()) {
                FarmTech farmTech = farmTechOptional.get();

                FarmTechResponse farmTechResponse = modelMapper.map(farmTech, FarmTechResponse.class);

                return new ApiResponse<>("Farmtech fetched successfully.", farmTechResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Farmtech not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching farmtech.", e);
            return new ApiResponse<>("An error occurred while fetching farmtech.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getFarmTechByUserId(Long userId) {
        try {
            Optional<FarmTech> farmTechOptional = farmTechRepository.findByDeletedFlagAndUserId(Constants.NO, userId);
            if (farmTechOptional.isPresent()) {
                FarmTech farmTech = farmTechOptional.get();

                FarmTechResponse farmTechResponse = modelMapper.map(farmTech, FarmTechResponse.class);

                return new ApiResponse<>("Farmtech fetched successfully.", farmTechResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Farmtech not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching farmtech.", e);
            return new ApiResponse<>("An error occurred while fetching farmtech.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> getAllFarmTechs() {
        try {
            List<FarmTech> farmTechList = farmTechRepository.findByDeletedFlag(Constants.NO);

            List<FarmTechResponse> farmTechResponses = farmTechList.stream().map(farmTech -> modelMapper
                            .map(farmTech, FarmTechResponse.class)).collect(Collectors.toList());

            return new ApiResponse<>("Farm tech fetched successfully", farmTechResponses,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching farm tech.", e);

            return new ApiResponse<>("An error occurred while fetching farm tech.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllFarmTechInValueChain(Long valueChainId) {
        try {
            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueChainId, Constants.NO);
            if (valueChainOptional.isEmpty()) {
                return new ApiResponse<>("Value chain not found", null, HttpStatus.NOT_FOUND.value());
            }
            ValueChain valueChain = valueChainOptional.get();
            List<FarmTech> farmTechList = farmTechRepository.findByValueChainsContaining(valueChain);
            List<FarmTechResponse> farmTechResponses = farmTechList
                    .stream()
                    .map(farmTech -> modelMapper
                            .map(farmTech, FarmTechResponse.class))
                    .toList();
            return new ApiResponse<>("Farm Tech fetched successfully", farmTechResponses, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error occurred: ", e);
            return new ApiResponse<>("Error occurred fetching farm tech", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> farmTechDelete(Long id) {
        try {
            Optional<FarmTech> farmTechOptional = farmTechRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (farmTechOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();
                FarmTech farmTech = farmTechOptional.get();
                farmTech.setDeletedFlag(Constants.YES);
                farmTech.setDeletedBy(currentUser);
                farmTech.setDeletedAt(LocalDateTime.now());

                User user = farmTech.getUser();
                user.setDeletedAt(LocalDateTime.now());
                user.setDeletedBy(currentUser);
                user.setDeletedFlag(Constants.YES);
                user.setActive(false);

                userRepository.save(user);
                farmTechRepository.save(farmTech);

                return new ApiResponse<>("Farm tech was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Farm tech not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while deleting farm tech.", e);

            return new ApiResponse<>("An error occurred while deleting farm tech.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> farmTechProfileUpdate(FarmTechRequest farmTechRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.",null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<FarmTech> farmTechOptional = farmTechRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (farmTechOptional.isPresent()) {

                FarmTech farmTech = farmTechOptional.get();
                User user = farmTech.getUser();

                if (farmTechRequest.getIdNumber() != null && farmTechRequest.getIdNumber().length() > 0
                        && !Objects.equals(farmTechRequest.getIdNumber(), farmTech.getIdNumber())) {
                    Optional<FarmTech> farmTechIdOptional = farmTechRepository
                            .findByIdNumber(farmTechRequest.getIdNumber());
                    if (farmTechIdOptional.isPresent()) {
                        return new ApiResponse<>("User with this Id Number already exists.", null,
                                HttpStatus.FORBIDDEN.value());
                    }
                    farmTech.setIdNumber(farmTechRequest.getIdNumber());
                }

                if (farmTechRequest.getValueChainIds() != null) {
                    if (farmTechRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    // replace existing value chain
                    Set<Long> newValueChainIds = new HashSet<>(farmTechRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = farmTech.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getFarmTechList().remove(farmTech);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : farmTechRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId,
                                Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist",
                                    null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!farmTech.getValueChains().contains(valueChain)) {
                            farmTech.getValueChains().add(valueChain);
                            valueChain.getFarmTechList().add(farmTech);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }

                farmTech.setFarmTechVerified(false);
                updateFarmTechFields(farmTech, farmTechRequest);
                updateWorkingHours(farmTech, farmTechRequest);
                updateFarmTechLocation(farmTech, farmTechRequest);
                updateWorkingDays(farmTech, farmTechRequest);

                FarmTech updatedFarmTech = farmTechRepository.save(farmTech);

                FarmTechResponse farmTechResponse = modelMapper.map(updatedFarmTech, FarmTechResponse.class);

                return new ApiResponse<>("FarmTech profile was successfully updated.",
                        farmTechResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("FarmTech not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating farmtech's profile.", e);

            return new ApiResponse<>("An error occurred while updating farmtech's profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());

        }
    }

    private void updateFarmTechFields(FarmTech farmTech, FarmTechRequest farmTechRequest) {
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getGender(),
                farmTech::setGender, farmTech::getGender);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getProfilePicture(),
                farmTech::setProfilePicture, farmTech::getProfilePicture);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getBio(),
                farmTech::setBio, farmTech::getBio);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getFarmTechName(),
                farmTech::setFarmTechName, farmTech::getFarmTechName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getFarmTechEmail(),
                farmTech::setFarmTechEmail, farmTech::getFarmTechEmail);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getFarmTechDescription(),
                farmTech::setFarmTechDescription, farmTech::getFarmTechDescription);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getFarmTechPhone(),
                farmTech::setFarmTechPhone, farmTech::getFarmTechPhone);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getLicenseNumber(),
                farmTech::setLicenseNumber, farmTech::getLicenseNumber);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getFarmTechLogo(),
                farmTech::setFarmTechLogo, farmTech::getFarmTechLogo);
    }

    private void updateWorkingDays(FarmTech farmTech, FarmTechRequest farmTechRequest) {
        if (farmTechRequest.getWorkingDays() != null) {
            farmTech.setWorkingDays(farmTechRequest.getWorkingDays());
        }
    }
    private void updateWorkingHours(FarmTech farmTech, FarmTechRequest farmTechRequest) {
        if (farmTechRequest.getWorkingHours() != null) {
            WorkingHours workingHours = farmTech.getWorkingHours();
            if (workingHours == null) {
                workingHours = new WorkingHours();
            }
            workingHours.setStartHour(farmTechRequest.getWorkingHours().getStartHour());
            workingHours.setEndHour(farmTechRequest.getWorkingHours().getEndHour());
            farmTech.setWorkingHours(workingHours);
        }
    }

    private void updateFarmTechLocation(FarmTech farmTech, FarmTechRequest farmTechRequest) {
        if (farmTechRequest.getFarmTechLocation() != null) {
            Cordinates coordinates = farmTech.getFarmTechLocation();
            if (coordinates == null) {
                coordinates = new Cordinates();
            }
            coordinates.setLatitude(farmTechRequest.getFarmTechLocation().getLatitude());
            coordinates.setLongitude(farmTechRequest.getFarmTechLocation().getLongitude());
            farmTech.setFarmTechLocation(coordinates);
        }
    }

    public ApiResponse<?> updateFarmTechByFarmTechId(FarmTechRequest farmTechRequest, Long farmTechId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<FarmTech> farmTechOptional = farmTechRepository.findByDeletedFlagAndId(Constants.NO, farmTechId);
            if (farmTechOptional.isPresent()) {
                FarmTech farmTech = farmTechOptional.get();
                if (farmTechRequest.getIdNumber() != null && farmTechRequest.getIdNumber().length() > 0
                        && !Objects.equals(farmTechRequest.getIdNumber(), farmTech.getIdNumber())) {
                    Optional<FarmTech> farmTechIdOptional = farmTechRepository
                            .findByIdNumber(farmTechRequest.getIdNumber());
                    if (farmTechIdOptional.isPresent()) {
                        return new ApiResponse<>("Id Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    farmTech.setIdNumber(farmTechRequest.getIdNumber());
                }

                if (farmTechRequest.getValueChainIds() != null) {
                    if (farmTechRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    // replace existing value chain
                    Set<Long> newValueChainIds = new HashSet<>(farmTechRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = farmTech.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getFarmTechList().remove(farmTech);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : farmTechRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId,
                                Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist",
                                    null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!farmTech.getValueChains().contains(valueChain)) {
                            farmTech.getValueChains().add(valueChain);
                            valueChain.getFarmTechList().add(farmTech);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }

                updateFarmTechFields(farmTech, farmTechRequest);
                updateWorkingHours(farmTech, farmTechRequest);
                updateFarmTechLocation(farmTech, farmTechRequest);
                updateWorkingDays(farmTech, farmTechRequest);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getEmail(),
                        farmTech.getUser()::setEmail, farmTech.getUser()::getEmail);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getPhoneNo(),
                        farmTech.getUser()::setPhoneNo, farmTech.getUser()::getPhoneNo);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getFarmTechVerified(),
                        farmTech::setFarmTechVerified, farmTech::getFarmTechVerified);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(farmTechRequest.getFarmTechVerified(),
                        farmTech::setFarmTechVerified, farmTech::getFarmTechVerified);

                FarmTech updatedFarmTech = farmTechRepository.save(farmTech);
                FarmTechResponse farmTechResponse = modelMapper.map(updatedFarmTech, FarmTechResponse.class);

                return new ApiResponse<>("FarmTech profile successfully updated.", farmTechResponse,
                        HttpStatus.OK.value());
            }
            return new ApiResponse<>("FarmTech not found", null, HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            log.info("Error updating FarmTech by farmTechId", e);

            return new ApiResponse<>("Error updating FarmTech by farmTechId", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
