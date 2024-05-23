package com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.EquiFarm.EquiFarm.ServiceProvider.Expertise.ExpertiseRepo;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices.DTO.TypeOfServiceRequest;
import com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices.DTO.TypeOfServiceResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TypeOfServicesService {
    private final TypeOfServicesRepository typeOfServicesRepository;

    private final ExpertiseRepo expertiseRepo;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> getAllTypesOfServices() {
        try {
            List<TypeOfServices> typeOfService = typeOfServicesRepository.findByDeletedFlag(Constants.NO);

            List<TypeOfServiceResponse> typeOfServicesResponse = typeOfService.stream()
                    .map(service -> modelMapper.map(service, TypeOfServiceResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Type of Services fetched Successfully.", typeOfServicesResponse,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching types of service.", e);
            return new ApiResponse<>("An error occurred while fetching types of service.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());

        }
    }

    @Transactional
    public ApiResponse<?> createTypeOfService(TypeOfServiceRequest typeOfServiceRequest) {
        try {
            Optional<TypeOfServices> typeOfServicesOptional = typeOfServicesRepository
                    .findByTypeOfServiceAndDeletedFlag(typeOfServiceRequest.getTypeOfService(), Constants.NO);

            if (typeOfServicesOptional.isPresent()) {
                return new ApiResponse<>("Type of Service already exists.", null, HttpStatus.BAD_REQUEST.value());
            }

            if (typeOfServiceRequest.getTypeOfService() == null) {
                return new ApiResponse<>("Type of Service is required.", null, HttpStatus.BAD_REQUEST.value());
            }

//            Optional<Expertise> expertiseOptional = expertiseRepo.findByIdAndDeletedFlag(typeOfServiceRequest.getExpertiseId(), Constants.NO);
//            if (expertiseOptional.isEmpty()) {
//                return new ApiResponse<>("Expertise Not Found", null, HttpStatus.BAD_REQUEST.value());
//            }
//            Expertise expertise = expertiseOptional.get();
            var typeOfService = TypeOfServices.builder()
                    .typeOfService(typeOfServiceRequest.getTypeOfService())
                    .description(typeOfServiceRequest.getDescription())
                    .servicesCategory(typeOfServiceRequest.getServicesCategory())
//                    .expertise(expertise)
                    .build();

            var savedTypeOfService = typeOfServicesRepository.save(typeOfService);

            TypeOfServiceResponse typeOfServiceResponse = modelMapper.map(
                    savedTypeOfService, TypeOfServiceResponse.class);

            return new ApiResponse<>("Type of Service added successfully.", typeOfServiceResponse,
                    HttpStatus.CREATED.value());

        } catch (Exception e) {
            log.error("An error occurred while creating type of service.", e);
            return new ApiResponse<>("An error occurred while creating type of service.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getTypeOfServiceById(Long id) {
        try {
            Optional<TypeOfServices> typeOfServiceOptional = typeOfServicesRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typeOfServiceOptional.isPresent()) {
                TypeOfServices typeOfServices = typeOfServiceOptional.get();

                TypeOfServiceResponse typeOfServiceResponse = modelMapper.map(typeOfServices,
                        TypeOfServiceResponse.class);

                return new ApiResponse<>("Type of service was successfully fetched.", typeOfServiceResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Type of service not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching type of service.", e);
            return new ApiResponse<>("An error occurred while fetching type of service.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> updateTypeOfService(TypeOfServiceRequest typeOfServiceRequest, Long id) {
        try {
            Optional<TypeOfServices> typeOfServiceOptional = typeOfServicesRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typeOfServiceOptional.isEmpty()) {
                return new ApiResponse<>("Type of service not found.", null, HttpStatus.NOT_FOUND.value());
            }
            TypeOfServices typeOfService = typeOfServiceOptional.get();

            if (typeOfServicesRepository.existsByTypeOfService(typeOfServiceRequest.getTypeOfService())) {
                return new ApiResponse<>("Type of Service already exists", null, HttpStatus.BAD_REQUEST.value());
            }
//            Optional<Expertise> expertiseOptional = expertiseRepo.findByIdAndDeletedFlag(typeOfServiceRequest.getExpertiseId(), Constants.NO);
//            if (expertiseOptional.isEmpty()){
//                return new ApiResponse<>("Expertise not found.", null, HttpStatus.NOT_FOUND.value());
//            }
//            Expertise expertise = expertiseOptional.get();
//
//            typeOfService.setExpertise(expertise);

            FieldUpdateUtil.updateFieldIfNotNullAndChanged(typeOfServiceRequest.getDescription(),
                    typeOfService::setDescription, typeOfService::getDescription);
            FieldUpdateUtil.updateFieldIfNotNullAndChanged(typeOfServiceRequest.getServicesCategory(),
                    typeOfService::setServicesCategory, typeOfService::getServicesCategory);
            FieldUpdateUtil.updateFieldIfNotNullAndChanged(typeOfServiceRequest.getTypeOfService(),
                    typeOfService::setTypeOfService, typeOfService::getTypeOfService);
            TypeOfServices updatedTypeOfServices = typeOfServicesRepository.save(typeOfService);

            TypeOfServiceResponse typeOfServiceResponse = modelMapper.map(updatedTypeOfServices,
                    TypeOfServiceResponse.class);

            return new ApiResponse<>("Type of Service updated successfully.", typeOfServiceResponse,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while updating type of service.", e);
            return new ApiResponse<>("An error occurred while updating type of service.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> typeOfServiceDelete(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<TypeOfServices> typeOfServiceOptional = typeOfServicesRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typeOfServiceOptional.isPresent()) {
                TypeOfServices typeOfServices = typeOfServiceOptional.get();
                typeOfServices.setDeletedAt(LocalDateTime.now());
                typeOfServices.setDeletedBy(currentUser);
                typeOfServices.setDeletedFlag(Constants.YES);

                typeOfServicesRepository.save(typeOfServices);

                return new ApiResponse<>("Type of service was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Type of Service not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting type of service.", e);

            return new ApiResponse<>("An error occurred while deleting type of service.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
