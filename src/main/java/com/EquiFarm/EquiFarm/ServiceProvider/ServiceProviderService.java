package com.EquiFarm.EquiFarm.ServiceProvider;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.ServiceProvider.DTO.ServiceProviderUserRequest;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.ServiceProviderUserResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.Expertise.Expertise;
import com.EquiFarm.EquiFarm.ServiceProvider.Expertise.ExpertiseRepo;
import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.ProvidedServicesRepo;
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

import javax.swing.text.html.Option;
import javax.validation.constraints.Null;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceProviderService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ProvidedServicesRepo providedServicesRepo;
    private final ExpertiseRepo expertiseRepo;
    private final ValueChainRepo valueChainRepo;

    public ApiResponse<?> getServiceProviderProfile() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository
                    .findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());

            if (serviceProviderOptional.isPresent()) {
                ServiceProvider serviceProvider = serviceProviderOptional.get();

                ServiceProviderUserResponse serviceProviderUserResponse = modelMapper.map(serviceProvider,
                        ServiceProviderUserResponse.class);

                return new ApiResponse<>("Service Provider profile fetched successfully", serviceProviderUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Service Provider not found.", null, HttpStatus.NOT_FOUND.value());

            }

        } catch (Exception e) {
            log.error("An error occurred while fetching service provider profile.", e);
            return new ApiResponse<>("An error occurred while fetching service provider profile", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getServiceProviderByProfileId(Long id) {
        try {
            Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (serviceProviderOptional.isPresent()) {
                ServiceProvider serviceProvider = serviceProviderOptional.get();

                ServiceProviderUserResponse serviceProviderUserResponse = modelMapper.map(serviceProvider,
                        ServiceProviderUserResponse.class);

                return new ApiResponse<>("Service Provider profile fetched successfully", serviceProviderUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Service Provider not found.", null, HttpStatus.NOT_FOUND.value());

            }
        } catch (Exception e) {
            log.error("An error occurred while fetching service provider.", e);

            return new ApiResponse<>("An error occurred while fetching service provider.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllServiceProviders() {
        try {
            List<ServiceProvider> serviceProvidersList = serviceProviderRepository.findByDeletedFlag(Constants.NO);

            List<ServiceProviderUserResponse> serviceProviderUserResponses;

//            if (ids != null && !ids.isEmpty()) {
//                serviceProviderUserResponses = serviceProvidersList
//                        .stream()
//                        .filter(provider -> ids.contains(provider.getId()))
//                        .map(serviceProvider -> modelMapper.map(serviceProvider, ServiceProviderUserResponse.class))
//                        .collect(Collectors.toList());
//            } else {
                serviceProviderUserResponses = serviceProvidersList
                        .stream()
                        .map(serviceProvider -> modelMapper.map(serviceProvider, ServiceProviderUserResponse.class))
                        .collect(Collectors.toList());
//            }

            return new ApiResponse<>("Service Providers fetched successfully", serviceProviderUserResponses,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching service providers.", e);

            return new ApiResponse<>("An error occurred while fetching service providers.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllServiceProvidersInValueChain(Long valueChainId){
        try {
            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueChainId, Constants.NO);
            if (valueChainOptional.isEmpty()){
                return new ApiResponse<>("Value chain not found", null, HttpStatus.NOT_FOUND.value());
            }
            ValueChain valueChain = valueChainOptional.get();
            List<ServiceProvider> serviceProviderList = serviceProviderRepository.findByValueChainsContaining(valueChain);
            List<ServiceProviderUserResponse> serviceProviderUserResponses = serviceProviderList
                    .stream()
                    .map(serviceProvider -> modelMapper
                            .map(serviceProvider, ServiceProviderUserResponse.class))
                    .toList();
            return new ApiResponse<>("Service providers fetched successfully", serviceProviderUserResponses, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error occurred: ", e);
            return new ApiResponse<>("Error occurred fetching service providers", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> serviceProviderProfileUpdate(ServiceProviderUserRequest serviceProviderUserRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository
                    .findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());

            if (serviceProviderOptional.isPresent()) {
                ServiceProvider serviceProvider = serviceProviderOptional.get();
                User user = serviceProvider.getUser();

                if (serviceProviderUserRequest.getIdNumber() != null
                        && serviceProviderUserRequest.getIdNumber().length() > 0
                        && !Objects.equals(serviceProviderUserRequest.getIdNumber(), serviceProvider.getIdNumber())) {
                    if (serviceProviderRepository.existsByIdNumber(serviceProviderUserRequest.getIdNumber())) {
                        return new ApiResponse<>("User with this Id Number already exists.", null,
                                HttpStatus.NOT_FOUND.value());
                    }
                    serviceProvider.setIdNumber(serviceProviderUserRequest.getIdNumber());
                }
                if (serviceProviderUserRequest.getPhoneNo() != null
                        && serviceProvider.getUser().getPhoneNo().length() > 0
                        && !Objects.equals(serviceProviderUserRequest.getPhoneNo(), serviceProvider.getUser().getPhoneNo())) {
                    Optional<User> userOptional = userRepository.findUserByPhoneNo(serviceProviderUserRequest.getPhoneNo());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Phone Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    serviceProvider.getUser().setPhoneNo(serviceProviderUserRequest.getPhoneNo());
                }
//                if(serviceProviderUserRequest.getExpertiseId()!=null) {
//                    Optional<Expertise> expertiseOptional = expertiseRepo.findByIdAndDeletedFlag(serviceProviderUserRequest.getExpertiseId(), Constants.NO);
//                    if (expertiseOptional.isPresent()) {
//                        Expertise expertise = expertiseOptional.get();
//                        serviceProvider.setExpertise(expertise);
//                    } else {
//                        return new ApiResponse<>(" Expertise Not Found", null, HttpStatus.NOT_FOUND.value());
//                    }
//                }

                if(serviceProviderUserRequest.getValueChainIds() != null) {
                    if (serviceProviderUserRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    // replace existing value chain
                    Set<Long> newValueChainIds = new HashSet<>(serviceProviderUserRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = serviceProvider.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getServiceProviderList().remove(serviceProvider);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long id : serviceProviderUserRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(id, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + id + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!serviceProvider.getValueChains().contains(valueChain)) {
                            serviceProvider.getValueChains().add(valueChain);
                            valueChain.getServiceProviderList().add(serviceProvider);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }
                serviceProvider.setAvailable(true);

                updateServiceProviderFields(serviceProvider, serviceProviderUserRequest);
                updateWorkingHours(serviceProvider, serviceProviderUserRequest);
                updateBusinessLocation(serviceProvider, serviceProviderUserRequest);
                updateWorkingDays(serviceProvider, serviceProviderUserRequest);

                ServiceProvider updatedServiceProvider = serviceProviderRepository.save(serviceProvider);

                ServiceProviderUserResponse serviceProviderUserResponse = modelMapper.map(updatedServiceProvider,
                        ServiceProviderUserResponse.class);

                return new ApiResponse<>("Service Provider profile was successfully updated.",
                        serviceProviderUserResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Service Provider not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating the service provider profile.", e);
            return new ApiResponse<>("An error occurred while updating the service provider profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void updateServiceProviderFields(ServiceProvider serviceProvider,
            ServiceProviderUserRequest serviceProviderUserRequest) {
//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getFirstName(),
//                serviceProvider.getUser()::setFirstName, serviceProvider.getUser()::getFirstName);
//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getLastName(),
//                serviceProvider.getUser()::setLastName, serviceProvider.getUser()::getLastName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getGender(),
                serviceProvider::setGender, serviceProvider::getGender);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getProfilePicture(),
                serviceProvider::setProfilePicture, serviceProvider::getProfilePicture);

        FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getBio(), serviceProvider::setBio,
                serviceProvider::getBio);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getBusinessName(),
                serviceProvider::setBusinessName, serviceProvider::getBusinessName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getBusinessEmail(),
                serviceProvider::setBusinessEmail, serviceProvider::getBusinessEmail);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getBusinessDescription(),
                serviceProvider::setBusinessDescription, serviceProvider::getBusinessDescription);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getBusinessPhone(),
                serviceProvider::setBusinessPhone, serviceProvider::getBusinessPhone);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getLicenseNumber(),
                serviceProvider::setLicenseNumber, serviceProvider::getLicenseNumber);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getBusinessLogo(),
                serviceProvider::setBusinessLogo, serviceProvider::getBusinessLogo);
    }

    private void updateWorkingDays(ServiceProvider serviceProvider,
            ServiceProviderUserRequest serviceProviderUserRequest) {
        if (serviceProviderUserRequest.getWorkingDays() != null) {
            serviceProvider.setWorkingDays(serviceProviderUserRequest.getWorkingDays());
        }
    }

    private void updateWorkingHours(ServiceProvider serviceProvider,
            ServiceProviderUserRequest serviceProviderUserRequest) {
        if (serviceProviderUserRequest.getWorkingHours() != null) {
            WorkingHours workingHours = serviceProvider.getWorkingHours();
            if (workingHours == null) {
                workingHours = new WorkingHours();
            }
            workingHours.setStartHour(serviceProviderUserRequest.getWorkingHours().getStartHour());
            workingHours.setEndHour(serviceProviderUserRequest.getWorkingHours().getEndHour());
            serviceProvider.setWorkingHours(workingHours);
        }
    }

    private void updateBusinessLocation(ServiceProvider serviceProvider,
            ServiceProviderUserRequest serviceProviderUserRequest) {
        if (serviceProviderUserRequest.getBusinessLocation() != null) {
            Cordinates coordinates = serviceProvider.getBusinessLocation();
            if (coordinates == null) {
                coordinates = new Cordinates();
            }
            coordinates.setLatitude(serviceProviderUserRequest.getBusinessLocation().getLatitude());
            coordinates.setLongitude(serviceProviderUserRequest.getBusinessLocation().getLongitude());
            serviceProvider.setBusinessLocation(coordinates);
        }
    }

    public ApiResponse<?> updateServiceProviderById(ServiceProviderUserRequest serviceProviderUserRequest, Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (serviceProviderOptional.isPresent()) {
                ServiceProvider serviceProvider = serviceProviderOptional.get();
                User user = serviceProvider.getUser();

                if (serviceProviderUserRequest.getIdNumber() != null
                        && serviceProviderUserRequest.getIdNumber().length() > 0
                        && !Objects.equals(serviceProviderUserRequest.getIdNumber(), serviceProvider.getIdNumber())) {
                    if (serviceProviderRepository.existsByIdNumber(serviceProviderUserRequest.getIdNumber())) {
                        return new ApiResponse<>("User with this Id Number already exists.", null,
                                HttpStatus.NOT_FOUND.value());
                    }
                    serviceProvider.setIdNumber(serviceProviderUserRequest.getIdNumber());
                }
                if (serviceProviderUserRequest.getPhoneNo() != null
                        && serviceProvider.getUser().getPhoneNo().length() > 0
                        && !Objects.equals(serviceProviderUserRequest.getPhoneNo(), serviceProvider.getUser().getPhoneNo())) {
                    Optional<User> userOptional = userRepository.findUserByPhoneNo(serviceProviderUserRequest.getPhoneNo());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Phone Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    serviceProvider.getUser().setPhoneNo(serviceProviderUserRequest.getPhoneNo());
                }
//                if(serviceProviderUserRequest.getExpertiseId()!=null) {
//                    Optional<Expertise> expertiseOptional = expertiseRepo.findByIdAndDeletedFlag(serviceProviderUserRequest.getExpertiseId(), Constants.NO);
//                    if (expertiseOptional.isPresent()) {
//                        Expertise expertise = expertiseOptional.get();
//                        serviceProvider.setExpertise(expertise);
//                    } else {
//                        return new ApiResponse<>(" Expertise Not Found", null, HttpStatus.NOT_FOUND.value());
//                    }
//                }

                if(serviceProviderUserRequest.getValueChainIds() != null) {
                    if (serviceProviderUserRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    Set<Long> newValueChainIds = new HashSet<>(serviceProviderUserRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = serviceProvider.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getServiceProviderList().remove(serviceProvider);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : serviceProviderUserRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!serviceProvider.getValueChains().contains(valueChain)) {
                            serviceProvider.getValueChains().add(valueChain);
                            valueChain.getServiceProviderList().add(serviceProvider);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }

                updateServiceProviderFields(serviceProvider, serviceProviderUserRequest);
                updateWorkingHours(serviceProvider, serviceProviderUserRequest);
                updateBusinessLocation(serviceProvider, serviceProviderUserRequest);
                updateWorkingDays(serviceProvider, serviceProviderUserRequest);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getBusinessVerified(),
                        serviceProvider::setBusinessVerified, serviceProvider::getBusinessVerified);

                ServiceProvider updatedServiceProvider = serviceProviderRepository.save(serviceProvider);

                ServiceProviderUserResponse serviceProviderUserResponse = modelMapper.map(updatedServiceProvider,
                        ServiceProviderUserResponse.class);

                return new ApiResponse<>("Service Provider profile was successfully updated.",
                        serviceProviderUserResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Service Provider not found", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while updating the service provider profile.", e);
            return new ApiResponse<>("An error occurred while updating the service provider profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> updateServiceProviderByUserId(Long userId, ServiceProviderUserRequest serviceProviderUserRequest) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return new ApiResponse<>("User Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            User user = userOptional.get();
            Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository.findByUser(user);
            if (serviceProviderOptional.isEmpty()) {
                return new ApiResponse<>("Service Provider Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            ServiceProvider serviceProvider = serviceProviderOptional.get();
            if (serviceProviderUserRequest.getIdNumber() != null
                    && serviceProviderUserRequest.getIdNumber().length() > 0
                    && !Objects.equals(serviceProviderUserRequest.getIdNumber(), serviceProvider.getIdNumber())) {
                if (serviceProviderRepository.existsByIdNumber(serviceProviderUserRequest.getIdNumber())) {
                    return new ApiResponse<>("User with this Id Number already exists.", null,
                            HttpStatus.NOT_FOUND.value());
                }
                serviceProvider.setIdNumber(serviceProviderUserRequest.getIdNumber());
            }
            if (serviceProviderUserRequest.getPhoneNo() != null
                    && serviceProvider.getUser().getPhoneNo().length() > 0
                    && !Objects.equals(serviceProviderUserRequest.getPhoneNo(), serviceProvider.getUser().getPhoneNo())) {
                Optional<User> userOptional1 = userRepository.findUserByPhoneNo(serviceProviderUserRequest.getPhoneNo());
                if (userOptional1.isPresent()) {
                    return new ApiResponse<>("Phone Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                }
                serviceProvider.getUser().setPhoneNo(serviceProviderUserRequest.getPhoneNo());
            }
            if (serviceProviderUserRequest.getEmail() != null
                    && serviceProvider.getUser().getEmail().length() > 0
                    && !Objects.equals(serviceProviderUserRequest.getEmail(), serviceProvider.getUser().getEmail())) {
                Optional<User> userOptional1 = userRepository.findByEmail(serviceProviderUserRequest.getEmail());
                if (userOptional1.isPresent()) {
                    return new ApiResponse<>("Email address already exists.", null, HttpStatus.NOT_MODIFIED.value());
                }
                serviceProvider.getUser().setEmail(serviceProviderUserRequest.getEmail());
            }
//            if(serviceProviderUserRequest.getValueChains().size() == 0){
//                return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
//            }
//            for(ValueChain valueChain: serviceProviderUserRequest.getValueChains()){
//                if(valueChain.getValueChain() != null){
//                    serviceProvider.getValueChains().add(valueChain);
//                }
//            }
            if(serviceProviderUserRequest.getValueChainIds() != null) {
                if (serviceProviderUserRequest.getValueChainIds().size() == 0) {
                    return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                }
                Set<Long> newValueChainIds = new HashSet<>(serviceProviderUserRequest.getValueChainIds());

                Iterator<ValueChain> iterator = serviceProvider.getValueChains().iterator();
                while (iterator.hasNext()) {
                    ValueChain existingValueChain = iterator.next();
                    if (!newValueChainIds.contains(existingValueChain.getId())) {
                        iterator.remove();
                        existingValueChain.getServiceProviderList().remove(serviceProvider);
                        valueChainRepo.save(existingValueChain);
                    }
                }
                for (Long valueId : serviceProviderUserRequest.getValueChainIds()) {
                    Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                    if (valueChainOptional.isEmpty()) {
                        return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                    }
                    ValueChain valueChain = valueChainOptional.get();
                    if (!serviceProvider.getValueChains().contains(valueChain)) {
                        serviceProvider.getValueChains().add(valueChain);
                        valueChain.getServiceProviderList().add(serviceProvider);
                        valueChainRepo.save(valueChain);
                    }
                }
            }

            updateServiceProviderFields(serviceProvider, serviceProviderUserRequest);
            updateWorkingHours(serviceProvider, serviceProviderUserRequest);
            updateBusinessLocation(serviceProvider, serviceProviderUserRequest);
            updateWorkingDays(serviceProvider, serviceProviderUserRequest);

            FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceProviderUserRequest.getBusinessVerified(),
                    serviceProvider::setBusinessVerified, serviceProvider::getBusinessVerified);

            ServiceProvider updatedServiceProvider = serviceProviderRepository.save(serviceProvider);

            ServiceProviderUserResponse serviceProviderResponse = modelMapper.map(updatedServiceProvider, ServiceProviderUserResponse.class);

            return new ApiResponse<>("ServiceProvider Updated Successfully", serviceProviderResponse, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("An error occurred Updating Service Provider: ", e);
            return new ApiResponse<>("An error occurred Updating Service Provider", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> fetchServiceProviderByUserId(Long userId) {
        try {
            Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository.findByDeletedFlagAndUserId(Constants.NO, userId);
            if (serviceProviderOptional.isEmpty()) {
                return new ApiResponse<>("ServiceProvider not Found", null, HttpStatus.NOT_FOUND.value());
            }
            ServiceProvider serviceProvider = serviceProviderOptional.get();
            ServiceProviderUserResponse serviceProviderResponse = modelMapper.map(serviceProvider, ServiceProviderUserResponse.class);
            return new ApiResponse<>("Success fetching serviceProvider by id", serviceProviderResponse, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error fetching serviceProvider by userId: ", e);
            return new ApiResponse<>("Error fetching serviceProvider by User Id", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> serviceProviderDelete(Long id) {
        try {
            Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (serviceProviderOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();
                ServiceProvider serviceProvider = serviceProviderOptional.get();
                serviceProvider.setDeletedFlag(Constants.YES);
                serviceProvider.setDeletedAt(LocalDateTime.now());
                serviceProvider.setAddedBy(currentUser);

                User user = serviceProvider.getUser();
                user.setDeletedAt(LocalDateTime.now());
                user.setDeletedBy(currentUser);
                user.setDeletedFlag(Constants.YES);
                user.setActive(false);

                userRepository.save(user);
                serviceProviderRepository.save(serviceProvider);

                return new ApiResponse<>("Service Provider was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Service Provider not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting service provider.", e);

            return new ApiResponse<>("An error occurred while deleting service provider.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
