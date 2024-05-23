package com.EquiFarm.EquiFarm.Insurance.TypeOfInsurance;

import com.EquiFarm.EquiFarm.Insurance.TypeOfInsurance.DTO.TypeOfInsuranceRequest;
import com.EquiFarm.EquiFarm.Insurance.TypeOfInsurance.DTO.TypeOfInsuranceResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TypeOfInsuranceService {
    private final TypeOfInsuranceRepository typeOfInsuranceRepository;
    private final ModelMapper modelMapper;

    public ApiResponse<?> getAllTypeOfInsurance() {
        try {
            List<TypeOfInsurance> typeOfInsurances = typeOfInsuranceRepository.findByDeletedFlag(Constants.NO);

            List<TypeOfInsuranceResponse> typeOfInsuranceResponses = typeOfInsurances.stream()
                    .map(typeOfInsurance -> modelMapper.map(typeOfInsurance, TypeOfInsuranceResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Type of Insurance fetched successfully.", typeOfInsuranceResponses,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching types of insurance.", e);
            return new ApiResponse<>("An error occurred while fetching types of insurance.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> createTypeOfInsurance(TypeOfInsuranceRequest typeOfInsuranceRequest) {
        try {
            Optional<TypeOfInsurance> typeOfInsuranceOptional = typeOfInsuranceRepository
                    .findByTypeOfInsuranceAndDeletedFlag(typeOfInsuranceRequest.getTypeOfInsurance(), Constants.NO);

            if (typeOfInsuranceOptional.isPresent()) {
                return new ApiResponse<>("Type of Insurance already exists.", null,
                        HttpStatus.BAD_REQUEST.value());
            }

            if (typeOfInsuranceRequest.getTypeOfInsurance() == null) {
                return new ApiResponse<>("Type of Insurance is required.", null,
                        HttpStatus.BAD_REQUEST.value());
            }

            var typeOfInsurance = TypeOfInsurance.builder()
                    .typeOfInsurance(typeOfInsuranceRequest.getTypeOfInsurance())
                    .description(typeOfInsuranceRequest.getDescription())
                    .build();

            var savedTypeOfInsurance = typeOfInsuranceRepository.save(typeOfInsurance);

            TypeOfInsuranceResponse typeOfInsuranceResponse = modelMapper.map(savedTypeOfInsurance,
                    TypeOfInsuranceResponse.class);

            return new ApiResponse<>("Type of Insurance added successfully.", typeOfInsuranceResponse,
                    HttpStatus.CREATED.value());

        } catch (Exception e) {
            log.error("An error occurred while creating type of insurance.", e);
            return new ApiResponse<>("An error occurred while creating type of insurance.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());

        }
    }

    public ApiResponse<?> getTypeOfInsuranceById(Long id) {
        try {
            Optional<TypeOfInsurance> typeOfInsuranceOptional = typeOfInsuranceRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typeOfInsuranceOptional.isPresent()) {
                TypeOfInsurance typeOfInsurance = typeOfInsuranceOptional.get();

                TypeOfInsuranceResponse typeOfInsuranceResponse = modelMapper.map(typeOfInsurance,
                        TypeOfInsuranceResponse.class);

                return new ApiResponse<>("Type of insurance was successfully fetched.",
                        typeOfInsuranceResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Type of insurance not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching type of insurance.", e);

            return new ApiResponse<>("An error occurred while fetching type of insurance.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> updateTypeOfInsurance(TypeOfInsuranceRequest typeOfInsuranceRequest, Long id) {
        try {
            Optional<TypeOfInsurance> typeOfInsuranceOptional = typeOfInsuranceRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (typeOfInsuranceOptional.isPresent()) {
                TypeOfInsurance typeOfInsurance = typeOfInsuranceOptional.get();

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(
                        typeOfInsuranceRequest.getDescription(),
                        typeOfInsurance::setDescription,
                        typeOfInsurance::getDescription);

                if (typeOfInsuranceRequest.getTypeOfInsurance() != null
                        && typeOfInsuranceRequest.getTypeOfInsurance().length() > 0
                        && !Objects.equals(typeOfInsurance.getTypeOfInsurance(),
                        typeOfInsuranceRequest.getTypeOfInsurance())) {
                    Optional<TypeOfInsurance> typeOfInsuranceOptional2 = typeOfInsuranceRepository
                            .findByTypeOfInsuranceAndDeletedFlag(typeOfInsuranceRequest.getTypeOfInsurance(),
                                    Constants.NO);

                    if (typeOfInsuranceOptional2.isPresent()) {
                        return new ApiResponse<>("Type of Insurance already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }

                    typeOfInsurance.setTypeOfInsurance(typeOfInsuranceRequest.getTypeOfInsurance());
                }

                TypeOfInsurance updatedTypeOfInsurance = typeOfInsuranceRepository.save(typeOfInsurance);

                TypeOfInsuranceResponse typeOfInsuranceResponse = modelMapper.map(updatedTypeOfInsurance,
                        TypeOfInsuranceResponse.class);

                return new ApiResponse<>("Type of Insurance updated successfully.", typeOfInsuranceResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Type of insurance not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating type of insurance.", e);
            return new ApiResponse<>("An error occurred while updating type of insurance.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> typeOfInsuranceDelete(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<TypeOfInsurance> typeOfInsuranceOptional = typeOfInsuranceRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (typeOfInsuranceOptional.isPresent()) {
                TypeOfInsurance typeOfInsurance = typeOfInsuranceOptional.get();

                typeOfInsurance.setDeletedAt(LocalDateTime.now());
                typeOfInsurance.setDeletedBy(currentUser);
                typeOfInsurance.setDeletedFlag(Constants.YES);

                typeOfInsuranceRepository.save(typeOfInsurance);

                return new ApiResponse<>("Type of insurance was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Type of insurance not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting type of insurance.", e);

            return new ApiResponse<>("An error occurred while deleting type of insurance.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
