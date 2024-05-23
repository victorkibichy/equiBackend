package com.EquiFarm.EquiFarm.AgriBusiness;

import com.EquiFarm.EquiFarm.AgriBusiness.DTO.AgriBusinessUserRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.DTO.AgriBusinessUserResponse;
import com.EquiFarm.EquiFarm.AgriBusiness.TypeOfBusiness.TypeOfBusiness;
import com.EquiFarm.EquiFarm.AgriBusiness.TypeOfBusiness.TypeOfBusinessRepository;
import com.EquiFarm.EquiFarm.Farmer.DTO.FarmersResponse;
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
public class AgriBusinessService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AgriBusinessRepository agriBusinessRepository;
    private final TypeOfBusinessRepository typeOfBusinessRepository;
    private final ValueChainRepo valueChainRepo;

    public ApiResponse<?> getAgriBusinessOwnerProfile() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<AgriBusiness> agriBusinessOptional = agriBusinessRepository
                    .findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());

            if (agriBusinessOptional.isPresent()) {
                AgriBusiness agriBusiness = agriBusinessOptional.get();

                AgriBusinessUserResponse agriBusinessUserResponse = modelMapper.map(
                        agriBusiness, AgriBusinessUserResponse.class);

                return new ApiResponse<>("Agribusiness fetched successfully.", agriBusinessUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Agribusiness not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching agribusiness.", e);
            return new ApiResponse<>("An error occurred while fetching agribusiness.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAgriBusinessOwnerById(Long id) {
        try {
            Optional<AgriBusiness> agriBusinessOptional = agriBusinessRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (agriBusinessOptional.isPresent()) {
                AgriBusiness agriBusiness = agriBusinessOptional.get();

                AgriBusinessUserResponse agriBusinessUserResponse = modelMapper.map(
                        agriBusiness, AgriBusinessUserResponse.class);

                return new ApiResponse<>("Agribusiness fetched successfully.", agriBusinessUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Agribusiness not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching agribusiness.", e);
            return new ApiResponse<>("An error occurred while fetching agribusiness.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAgriBusinessOwnerByUserId(Long userId) {
        try {
            Optional<AgriBusiness> agriBusinessOptional = agriBusinessRepository
                    .findByDeletedFlagAndUserId(Constants.NO, userId);
            if (agriBusinessOptional.isPresent()) {
                AgriBusiness agriBusiness = agriBusinessOptional.get();

                AgriBusinessUserResponse agriBusinessUserResponse = modelMapper.map(
                        agriBusiness, AgriBusinessUserResponse.class);

                return new ApiResponse<>("Agribusiness fetched successfully.", agriBusinessUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Agribusiness not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching agribusiness.", e);
            return new ApiResponse<>("An error occurred while fetching agribusiness.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllAgribusinesses() {
        try {
            List<AgriBusiness> agribusinessList = agriBusinessRepository.findByDeletedFlag(Constants.NO);

            List<AgriBusinessUserResponse> agriBusinessUserResponses = agribusinessList.stream()
                    .map(agriBusiness -> modelMapper.map(agriBusiness, AgriBusinessUserResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Agri-Business fetched successfully", agriBusinessUserResponses,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching agribusinesses.", e);

            return new ApiResponse<>("An error occurred while fetching agribusinesses.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllAgribusinessInValueChain(Long valueChainId) {
        try {
            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueChainId, Constants.NO);
            if (valueChainOptional.isEmpty()) {
                return new ApiResponse<>("Value chain not found", null, HttpStatus.NOT_FOUND.value());
            }
            ValueChain valueChain = valueChainOptional.get();
            List<AgriBusiness> agriBusinessList = agriBusinessRepository.findByValueChainsContaining(valueChain);
            List<AgriBusinessUserResponse> agriBusinessUserResponses = agriBusinessList
                    .stream()
                    .map(agribusiness -> modelMapper
                            .map(agribusiness, AgriBusinessUserResponse.class))
                    .toList();
            return new ApiResponse<>("Agribusiness fetched successfully", agriBusinessUserResponses, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error occurred: ", e);
            return new ApiResponse<>("Error occurred fetching agribusiness", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    public ApiResponse<?> agriBusinessUpdate(AgriBusinessUserRequest agriBusinessUserRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<AgriBusiness> agriBusinessOptional = agriBusinessRepository.findByDeletedFlagAndUserId(Constants.NO,currentUser.getId());
            if (agriBusinessOptional.isPresent()) {
                AgriBusiness agriBusiness = agriBusinessOptional.get();

                if (agriBusinessUserRequest.getIdNumber() != null
                        && agriBusinessUserRequest.getIdNumber().length() > 0
                        && !Objects.equals(agriBusinessUserRequest.getIdNumber(), agriBusiness.getIdNumber())) {
                    Optional<AgriBusiness> agriBusinessIdOptional = agriBusinessRepository

                            .findByIdNumber(agriBusinessUserRequest.getIdNumber());
                    if (agriBusinessIdOptional.isPresent()) {
                        return new ApiResponse<>("User with this Id Number already exists.", null,
                                HttpStatus.NOT_FOUND.value());
                    }
                    agriBusiness.setIdNumber(agriBusinessUserRequest.getIdNumber());
                }

                if (agriBusinessUserRequest.getTypeOfBusinessId() != null) {
                    Optional<TypeOfBusiness> typeOfBusinessOptional = typeOfBusinessRepository.findByDeletedFlagAndId(
                            Constants.NO, agriBusinessUserRequest.getTypeOfBusinessId());
                    if (typeOfBusinessOptional.isEmpty()) {
                        return new ApiResponse<>("Type of Business not found.", null, HttpStatus.NOT_FOUND.value());
                    }

                    TypeOfBusiness typeOfBusiness = typeOfBusinessOptional.get();
                    agriBusiness.setTypeOfBusiness(typeOfBusiness);

                }
                if (agriBusinessUserRequest.getValueChainIds() != null){
                    if(agriBusinessUserRequest.getValueChainIds().size() == 0){
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    Set<Long> newValueChainIds = new HashSet<>(agriBusinessUserRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = agriBusiness.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getAgriBusinessList().remove(agriBusiness);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for(Long valueId: agriBusinessUserRequest.getValueChainIds()){
                        Optional<ValueChain> valueChainOptional1 = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional1.isEmpty()){
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional1.get();
                        if(!agriBusiness.getValueChains().contains(valueChain)) {
                            agriBusiness.getValueChains().add(valueChain);
                            valueChain.getAgriBusinessList().add(agriBusiness);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }
                updateAgriBusinessFields(agriBusiness, agriBusinessUserRequest);
                updateWorkingHours(agriBusiness, agriBusinessUserRequest);
                updateBusinessLocation(agriBusiness, agriBusinessUserRequest);
                updateWorkingDays(agriBusiness, agriBusinessUserRequest);

                AgriBusiness updatedAgriBusiness = agriBusinessRepository.save(agriBusiness);

                AgriBusinessUserResponse agriBusinessUserResponse = modelMapper.map(updatedAgriBusiness,
                        AgriBusinessUserResponse.class);

                return new ApiResponse<>("Agri-business was successfully updated.", agriBusinessUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Agri-business not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while updating the agri-business.", e);
            return new ApiResponse<>("An error occurred while updating the agri-business.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void updateAgriBusinessFields(AgriBusiness agriBusiness,
            AgriBusinessUserRequest agriBusinessUserRequest) {
//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getFirstName(),
//                agriBusiness.getUser()::setFirstName, agriBusiness.getUser()::getFirstName);
//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getLastName(),
//                agriBusiness.getUser()::setLastName, agriBusiness.getUser()::getLastName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getGender(),
                agriBusiness::setGender, agriBusiness::getGender);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getProfilePicture(),
                agriBusiness::setProfilePicture, agriBusiness::getProfilePicture);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getBio(), agriBusiness::setBio,
                agriBusiness::getBio);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getBusinessName(),
                agriBusiness::setBusinessName, agriBusiness::getBusinessName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getBusinessEmail(),
                agriBusiness::setBusinessEmail, agriBusiness::getBusinessEmail);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getBusinessDescription(),
                agriBusiness::setBusinessDescription, agriBusiness::getBusinessDescription);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getBusinessPhone(),
                agriBusiness::setBusinessPhone, agriBusiness::getBusinessPhone);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getLicenseNumber(),
                agriBusiness::setLicenseNumber, agriBusiness::getLicenseNumber);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getBusinessLogo(),
                agriBusiness::setBusinessLogo, agriBusiness::getBusinessLogo);
    }

    private void updateWorkingDays(AgriBusiness agriBusiness,
            AgriBusinessUserRequest agriBusinessUserRequest) {
        if (agriBusinessUserRequest.getWorkingDays() != null) {
            agriBusiness.setWorkingDays(agriBusinessUserRequest.getWorkingDays());
        }
    }

    private void updateWorkingHours(AgriBusiness agriBusiness,
            AgriBusinessUserRequest agriBusinessUserRequest) {
        if (agriBusinessUserRequest.getWorkingHours() != null) {
            WorkingHours workingHours = agriBusiness.getWorkingHours();
            if (workingHours == null) {
                workingHours = new WorkingHours();
            }
            workingHours.setStartHour(agriBusinessUserRequest.getWorkingHours().getStartHour());
            workingHours.setEndHour(agriBusinessUserRequest.getWorkingHours().getEndHour());
            agriBusiness.setWorkingHours(workingHours);
        }
    }

    private void updateBusinessLocation(AgriBusiness agriBusiness,
            AgriBusinessUserRequest agriBusinessUserRequest) {
        if (agriBusinessUserRequest.getBusinessLocation() != null) {
            Cordinates coordinates = agriBusiness.getBusinessLocation();
            if (coordinates == null) {
                coordinates = new Cordinates();
            }
            coordinates.setLatitude(agriBusinessUserRequest.getBusinessLocation().getLatitude());
            coordinates.setLongitude(agriBusinessUserRequest.getBusinessLocation().getLongitude());
            agriBusiness.setBusinessLocation(coordinates);
        }
    }

    public ApiResponse<?> updateAgriBusiness(Long id, AgriBusinessUserRequest agriBusinessUserRequest) {
        try {
            Optional<AgriBusiness> agriBusinessOptional = agriBusinessRepository.findByDeletedFlagAndId(Constants.NO,
                    id);
            if (agriBusinessOptional.isPresent()) {
                AgriBusiness agriBusiness = agriBusinessOptional.get();

                if (agriBusinessUserRequest.getIdNumber() != null
                        && agriBusinessUserRequest.getIdNumber().length() > 0
                        && !Objects.equals(agriBusinessUserRequest.getIdNumber(), agriBusiness.getIdNumber())) {
                    Optional<AgriBusiness> agriBusinessIdOptional = agriBusinessRepository

                            .findByIdNumber(agriBusinessUserRequest.getIdNumber());
                    if (agriBusinessIdOptional.isPresent()) {
                        return new ApiResponse<>("User with this Id Number already exists.", null,
                                HttpStatus.NOT_FOUND.value());
                    }
                    agriBusiness.setIdNumber(agriBusinessUserRequest.getIdNumber());
                }

                if (agriBusinessUserRequest.getTypeOfBusinessId() != null) {
                    Optional<TypeOfBusiness> typeOfBusinessOptional = typeOfBusinessRepository.findByDeletedFlagAndId(
                            Constants.NO, agriBusinessUserRequest.getTypeOfBusinessId());
                    if (typeOfBusinessOptional.isEmpty()) {
                        return new ApiResponse<>("Type of Business not found.", null, HttpStatus.NOT_FOUND.value());
                    }

                    TypeOfBusiness typeOfBusiness = typeOfBusinessOptional.get();
                    agriBusiness.setTypeOfBusiness(typeOfBusiness);

                }
                if (agriBusinessUserRequest.getValueChainIds() != null) {
                    if (agriBusinessUserRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    Set<Long> newValueChainIds = new HashSet<>(agriBusinessUserRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = agriBusiness.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getAgriBusinessList().remove(agriBusiness);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : agriBusinessUserRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional1 = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional1.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional1.get();
                        if (!agriBusiness.getValueChains().contains(valueChain)) {
                            agriBusiness.getValueChains().add(valueChain);
                            valueChain.getAgriBusinessList().add(agriBusiness);
                            valueChainRepo.save(valueChain);
                        }
                    }
            }
                updateAgriBusinessFields(agriBusiness, agriBusinessUserRequest);
                updateWorkingHours(agriBusiness, agriBusinessUserRequest);
                updateBusinessLocation(agriBusiness, agriBusinessUserRequest);
                updateWorkingDays(agriBusiness, agriBusinessUserRequest);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getEmail(),
                        agriBusiness.getUser()::setEmail, agriBusiness.getUser()::getEmail);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getPhoneNo(),
                        agriBusiness.getUser()::setPhoneNo, agriBusiness.getUser()::getPhoneNo);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getBusinessVerified(),
                        agriBusiness::setBusinessVerified, agriBusiness::getBusinessVerified);

                AgriBusiness updatedAgriBusiness = agriBusinessRepository.save(agriBusiness);

                AgriBusinessUserResponse agriBusinessUserResponse = modelMapper.map(updatedAgriBusiness,
                        AgriBusinessUserResponse.class);

                return new ApiResponse<>("Agri-business was successfully updated.", agriBusinessUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Agri-business not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating agribusiness.", e);
            return new ApiResponse<>("An error occurred while updating agribusiness.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> updateAgriBusinessByUserId(Long userId, AgriBusinessUserRequest agriBusinessUserRequest) {
        try {
            Optional<AgriBusiness> agriBusinessOptional = agriBusinessRepository.findByDeletedFlagAndUserId(Constants.NO,
                    userId);
            if (agriBusinessOptional.isPresent()) {
                AgriBusiness agriBusiness = agriBusinessOptional.get();

                if (agriBusinessUserRequest.getIdNumber() != null
                        && agriBusinessUserRequest.getIdNumber().length() > 0
                        && !Objects.equals(agriBusinessUserRequest.getIdNumber(), agriBusiness.getIdNumber())) {
                    Optional<AgriBusiness> agriBusinessIdOptional = agriBusinessRepository

                            .findByIdNumber(agriBusinessUserRequest.getIdNumber());
                    if (agriBusinessIdOptional.isPresent()) {
                        return new ApiResponse<>("User with this Id Number already exists.", null,
                                HttpStatus.NOT_FOUND.value());
                    }
                    agriBusiness.setIdNumber(agriBusinessUserRequest.getIdNumber());
                }

                if (agriBusinessUserRequest.getTypeOfBusinessId() != null) {
                    Optional<TypeOfBusiness> typeOfBusinessOptional = typeOfBusinessRepository.findByDeletedFlagAndId(
                            Constants.NO, agriBusinessUserRequest.getTypeOfBusinessId());
                    if (typeOfBusinessOptional.isEmpty()) {
                        return new ApiResponse<>("Type of Business not found.", null, HttpStatus.NOT_FOUND.value());
                    }

                    TypeOfBusiness typeOfBusiness = typeOfBusinessOptional.get();
                    agriBusiness.setTypeOfBusiness(typeOfBusiness);

                }
                if (agriBusinessUserRequest.getValueChainIds() != null){
                    if(agriBusinessUserRequest.getValueChainIds().size() == 0){
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    Set<Long> newValueChainIds = new HashSet<>(agriBusinessUserRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = agriBusiness.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getAgriBusinessList().remove(agriBusiness);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for(Long valueId: agriBusinessUserRequest.getValueChainIds()){
                        Optional<ValueChain> valueChainOptional1 = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional1.isEmpty()){
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional1.get();
                        if(!agriBusiness.getValueChains().contains(valueChain)) {
                            agriBusiness.getValueChains().add(valueChain);
                            valueChain.getAgriBusinessList().add(agriBusiness);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }
                updateAgriBusinessFields(agriBusiness, agriBusinessUserRequest);
                updateWorkingHours(agriBusiness, agriBusinessUserRequest);
                updateBusinessLocation(agriBusiness, agriBusinessUserRequest);
                updateWorkingDays(agriBusiness, agriBusinessUserRequest);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getEmail(),
                        agriBusiness.getUser()::setEmail, agriBusiness.getUser()::getEmail);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getPhoneNo(),
                        agriBusiness.getUser()::setPhoneNo, agriBusiness.getUser()::getPhoneNo);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(agriBusinessUserRequest.getBusinessVerified(),
                        agriBusiness::setBusinessVerified, agriBusiness::getBusinessVerified);

                AgriBusiness updatedAgriBusiness = agriBusinessRepository.save(agriBusiness);

                AgriBusinessUserResponse agriBusinessUserResponse = modelMapper.map(updatedAgriBusiness,
                        AgriBusinessUserResponse.class);

                return new ApiResponse<>("Agri-business was successfully updated.", agriBusinessUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Agri-business not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating agribusiness.", e);
            return new ApiResponse<>("An error occurred while updating agribusiness.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> agriBusinessDelete(Long id) {
        try {
            Optional<AgriBusiness> agriBusinessOptional = agriBusinessRepository.findByDeletedFlagAndId(Constants.NO,
                    id);
            if (agriBusinessOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();
                AgriBusiness agriBusiness = agriBusinessOptional.get();
                agriBusiness.setDeletedFlag(Constants.YES);
                agriBusiness.setDeletedBy(currentUser);
                agriBusiness.setDeletedAt(LocalDateTime.now());

                User user = agriBusiness.getUser();
                user.setDeletedAt(LocalDateTime.now());
                user.setDeletedBy(currentUser);
                user.setDeletedFlag(Constants.YES);
                user.setActive(false);

                userRepository.save(user);
                agriBusinessRepository.save(agriBusiness);

                return new ApiResponse<>("Agribusiness was successfully deleted.", null, HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Agri-business not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while deleting agribusiness.", e);

            return new ApiResponse<>("An error occurred while deleting agribusiness.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
