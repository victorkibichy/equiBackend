package com.EquiFarm.EquiFarm.AgriBusiness.TypeOfBusiness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.AgriBusiness.TypeOfBusiness.DTO.TypeOfBusinessRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.TypeOfBusiness.DTO.TypeOfBusinessResponse;
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
@Transactional
public class TypeOfBusinessService {
    private final TypeOfBusinessRepository typeOfBusinessRepository;
    private final ModelMapper modelMapper;

    public ApiResponse<?> getAllTypeOfBusiness() {
        try {
            List<TypeOfBusiness> typeOfBusinesses = typeOfBusinessRepository.findByDeletedFlag(Constants.NO);

            List<TypeOfBusinessResponse> typeOfBusinessResponses = typeOfBusinesses.stream()
                    .map(typeOfBusiness -> modelMapper.map(typeOfBusiness, TypeOfBusinessResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Type of Business fetched successfully.", typeOfBusinessResponses,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching types of businesses.", e);
            return new ApiResponse<>("An error occurred while fetching types of businesses.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> createTypeOfBusiness(TypeOfBusinessRequest typeOfBusinessRequest) {
        try {
            Optional<TypeOfBusiness> typeOfBusinessOptional = typeOfBusinessRepository
                    .findByTypeOfBusinessAndDeletedFlag(typeOfBusinessRequest.getTypeOfBusiness(), Constants.NO);

            if (typeOfBusinessOptional.isPresent()) {
                return new ApiResponse<>("Type of Business already exists.", null, HttpStatus.BAD_REQUEST.value());
            }

            if (typeOfBusinessRequest.getTypeOfBusiness() == null) {
                return new ApiResponse<>("Type of Business is required.", null, HttpStatus.BAD_REQUEST.value());
            }

            var typeOfBusiness = TypeOfBusiness.builder()
                    .typeOfBusiness(typeOfBusinessRequest.getTypeOfBusiness())
                    .description(typeOfBusinessRequest.getDescription())
                    .build();

            var savedTypeOfBusiness = typeOfBusinessRepository.save(typeOfBusiness);

            TypeOfBusinessResponse typeOfBusinessResponse = modelMapper.map(savedTypeOfBusiness,
                    TypeOfBusinessResponse.class);

            return new ApiResponse<>("Type of Business added successfully.", typeOfBusinessResponse,
                    HttpStatus.CREATED.value());

        } catch (Exception e) {
            log.error("An error occurred while creating type of business.", e);
            return new ApiResponse<>("An error occurred while creating type of business.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());

        }
    }

    public ApiResponse<?> getTypeOfBusinessById(Long id) {
        try {
            Optional<TypeOfBusiness> typeOfBusinessOptional = typeOfBusinessRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typeOfBusinessOptional.isPresent()) {
                TypeOfBusiness typeOfBusiness = typeOfBusinessOptional.get();

                TypeOfBusinessResponse typeOfBusinessResponse = modelMapper.map(typeOfBusiness,
                        TypeOfBusinessResponse.class);

                return new ApiResponse<>("Type of business was fetched successfully.", typeOfBusinessResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Type of business not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching type of business.", e);
            return new ApiResponse<>("An error occurred while fetching type of business.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> updateTypeOfBusiness(TypeOfBusinessRequest typeOfBusinessRequest, Long id) {
        try {
            Optional<TypeOfBusiness> typeOfBusinessOptional = typeOfBusinessRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (typeOfBusinessOptional.isPresent()) {
                TypeOfBusiness typeOfBusiness = typeOfBusinessOptional.get();

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(
                        typeOfBusinessRequest.getDescription(),
                        typeOfBusiness::setDescription,
                        typeOfBusiness::getDescription);

                if (typeOfBusinessRequest.getTypeOfBusiness() != null
                        && typeOfBusinessRequest.getTypeOfBusiness().length() > 0
                        && !Objects.equals(typeOfBusiness.getTypeOfBusiness(),
                                typeOfBusinessRequest.getTypeOfBusiness())) {
                    Optional<TypeOfBusiness> typeOfBusinessOptional2 = typeOfBusinessRepository
                            .findByTypeOfBusinessAndDeletedFlag(typeOfBusinessRequest.getTypeOfBusiness(),
                                    Constants.NO);

                    if (typeOfBusinessOptional2.isPresent()) {
                        return new ApiResponse<>("Type of Business already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }

                    typeOfBusiness.setTypeOfBusiness(typeOfBusinessRequest.getTypeOfBusiness());
                }

                TypeOfBusiness updatedTypeOfBusiness = typeOfBusinessRepository.save(typeOfBusiness);

                TypeOfBusinessResponse typeOfBusinessResponse = modelMapper.map(updatedTypeOfBusiness,
                        TypeOfBusinessResponse.class);

                return new ApiResponse<>("Type of Business updated successfully.", typeOfBusinessResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Type of business not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating type of business.", e);
            return new ApiResponse<>("An error occurred while updating type of business.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> typeOfBusinessDelete(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<TypeOfBusiness> typeOfBusinessOptional = typeOfBusinessRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typeOfBusinessOptional.isPresent()) {
                TypeOfBusiness typeOfBusiness = typeOfBusinessOptional.get();

                typeOfBusiness.setDeletedAt(LocalDateTime.now());
                typeOfBusiness.setDeletedBy(currentUser);
                typeOfBusiness.setDeletedFlag(Constants.YES);

                typeOfBusinessRepository.save(typeOfBusiness);

                return new ApiResponse<>("Type of business was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Type of business not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting type of business.", e);

            return new ApiResponse<>("An error occurred while deleting type of business.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
