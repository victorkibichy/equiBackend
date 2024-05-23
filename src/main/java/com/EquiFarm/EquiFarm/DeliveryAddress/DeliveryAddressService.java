package com.EquiFarm.EquiFarm.DeliveryAddress;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.DeliveryAddress.DTO.DeliveryAddressRequest;
import com.EquiFarm.EquiFarm.DeliveryAddress.DTO.DeliveryAddressResponse;
import com.EquiFarm.EquiFarm.DeliveryAddress.DTO.DeliveryAddressRequest.DeliveryAddressRequestBuilder;
import com.EquiFarm.EquiFarm.DeliveryAddress.DTO.DeliveryAddressResponse.DeliveryAddressResponseBuilder;
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
public class DeliveryAddressService {
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> addDeliveryAddress(DeliveryAddressRequest deliveryAddressRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return unauthorizedResponse("Access denied. User is not authenticated.");
            }

            if (deliveryAddressRequest.getAddressName() == null) {
                return badRequestResponse("Location name is required.");
            }

            if (deliveryAddressExists(currentUser.getId(), deliveryAddressRequest.getAddressName())) {
                return badRequestResponse("You already have this location name.");
            }

            handleDefaultAddress(currentUser.getId(), deliveryAddressRequest.isDefaultAddress());

            DeliveryAddress addedDeliveryAddress = saveDeliveryAddress(currentUser, deliveryAddressRequest);

            DeliveryAddressResponse deliveryAddressResponse = mapToResponse(addedDeliveryAddress);

            return successResponse("Delivery address was successfully added.", deliveryAddressResponse);
        } catch (Exception e) {
            log.error("An error occurred while adding a delivery address", e);
            return errorResponse("An error occurred while adding a delivery address");
        }
    }

    private ApiResponse<?> unauthorizedResponse(String message) {
        return new ApiResponse<>(message, null, HttpStatus.UNAUTHORIZED.value());
    }

    private ApiResponse<?> badRequestResponse(String message) {
        return new ApiResponse<>(message, null, HttpStatus.BAD_REQUEST.value());
    }

    private boolean deliveryAddressExists(long userId, String addressName) {
        return deliveryAddressRepository.findByDeletedFlagAndUserIdAndAddressName(Constants.NO, userId, addressName)
                .isPresent();
    }

    private void handleDefaultAddress(long userId, boolean isDefaultAddress) {
        if (isDefaultAddress) {
            List<DeliveryAddress> defaultAddresses = deliveryAddressRepository
                    .findByDeletedFlagAndUserIdAndIsDefaultAddress(Constants.NO, userId, true);
            if (!defaultAddresses.isEmpty()) {
                defaultAddresses.forEach(address -> address.setDefaultAddress(false));
                deliveryAddressRepository.saveAll(defaultAddresses);
            }
        }
    }

    private DeliveryAddress saveDeliveryAddress(User currentUser, DeliveryAddressRequest deliveryAddressRequest) {
        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .address(deliveryAddressRequest.getAddress())
                .addressName(deliveryAddressRequest.getAddressName())
                .isDefaultAddress(deliveryAddressRequest.isDefaultAddress())
                .latitude(deliveryAddressRequest.getLatitude())
                .longitude(deliveryAddressRequest.getLongitude())
                .user(currentUser)
                .build();
        return deliveryAddressRepository.save(deliveryAddress);
    }

    private DeliveryAddressResponse mapToResponse(DeliveryAddress deliveryAddress) {
        return modelMapper.map(deliveryAddress, DeliveryAddressResponse.class);
    }

    private ApiResponse<?> successResponse(String message, Object data) {
        return new ApiResponse<>(message, data, HttpStatus.CREATED.value());
    }

    private ApiResponse<?> errorResponse(String message) {
        return new ApiResponse<>(message, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Transactional
    public ApiResponse<?> getAllDeliveryAddress() {
        try {
            List<DeliveryAddress> deliveryAddressesList = deliveryAddressRepository.findByDeletedFlag(Constants.NO);

            List<DeliveryAddressResponse> deliveryAddressResponsesList = deliveryAddressesList.stream()
                    .map(delivery -> modelMapper.map(delivery, DeliveryAddressResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Delivery addresses fetched successfully.", deliveryAddressResponsesList,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching delivery addresses.", e);
            return new ApiResponse<>("An error occurred while fetching delivery addresses.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getDeliveryAddressById(Long id) {
        try {
            Optional<DeliveryAddress> deliveryAddressOptional = deliveryAddressRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (deliveryAddressOptional.isPresent()) {
                DeliveryAddress deliveryAddress = deliveryAddressOptional.get();

                DeliveryAddressResponse deliveryAddressResponse = modelMapper.map(deliveryAddress,
                        DeliveryAddressResponse.class);

                return new ApiResponse<>("Delivery Address successfully fetched.", deliveryAddressResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Delivery Address not found.", null, HttpStatus.NOT_FOUND.value());

            }

        } catch (Exception e) {
            log.error("An error occurred while fetching delivery addresses.", e);
            return new ApiResponse<>("An error occurred while fetching delivery addresses.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> updateDeliveryAddress(DeliveryAddressRequest deliveryAddressRequest, Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<DeliveryAddress> deliveryAddressOptional = deliveryAddressRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (deliveryAddressOptional.isPresent()) {
                DeliveryAddress deliveryAddress = deliveryAddressOptional.get();

                if (deliveryAddressRequest.getAddressName() != null
                        && deliveryAddressRequest.getAddressName().length() > 0
                        && !Objects.equals(deliveryAddress.getAddressName(), deliveryAddressRequest.getAddressName())) {
                    Optional<DeliveryAddress> deliveryAddressOptional1 = deliveryAddressRepository
                            .findByDeletedFlagAndUserIdAndAddressName(Constants.NO, currentUser.getId(),
                                    deliveryAddressRequest.getAddressName());

                    if (deliveryAddressOptional1.isPresent()) {
                        return new ApiResponse<>("You already have an address with a similar location name.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    deliveryAddress.setAddressName(deliveryAddressRequest.getAddressName());
                }

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(
                        deliveryAddressRequest.getAddress(),
                        deliveryAddress::setAddress,
                        deliveryAddress::getAddress);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(
                        deliveryAddressRequest.getLatitude(),
                        deliveryAddress::setLatitude,
                        deliveryAddress::getLatitude);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(
                        deliveryAddressRequest.getLongitude(),
                        deliveryAddress::setLongitude,
                        deliveryAddress::getLongitude);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(
                        deliveryAddressRequest.getAddress(),
                        deliveryAddress::setAddress,
                        deliveryAddress::getAddress);
                handleDefaultAddress(currentUser.getId(), deliveryAddressRequest.isDefaultAddress());
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(
                        deliveryAddressRequest.isDefaultAddress(),
                        deliveryAddress::setDefaultAddress,
                        deliveryAddress::isDefaultAddress);

                DeliveryAddress updatedDeliveryAddress = deliveryAddressRepository.save(deliveryAddress);

                DeliveryAddressResponse deliveryAddressResponse = modelMapper.map(updatedDeliveryAddress,
                        DeliveryAddressResponse.class);

                return new ApiResponse<>("Delivery address updated successfully.", deliveryAddressResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Delivery address not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while updating delivery addresses.", e);
            return new ApiResponse<>("An error occurred while updating delivery addresses.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getUserDeliveryAddresses() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            List<DeliveryAddress> deliveryAddresses = deliveryAddressRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            List<DeliveryAddressResponse> deliveryAddressesResponses = deliveryAddresses.stream()
                    .map(address -> modelMapper.map(address, DeliveryAddressResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("User addresses fetched successfully.", deliveryAddressesResponses,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching user delivery addresses.", e);
            return new ApiResponse<>("An error occurred while fetching user delivery addresses.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> deleteDeliveryAddress(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<DeliveryAddress> deliveryAddressOptional = deliveryAddressRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (deliveryAddressOptional.isPresent()) {
                DeliveryAddress deliveryAddress = deliveryAddressOptional.get();

                deliveryAddress.setDeletedAt(LocalDateTime.now());
                deliveryAddress.setDeletedBy(currentUser);
                deliveryAddress.setDeletedFlag(Constants.YES);

                deliveryAddressRepository.save(deliveryAddress);
                return new ApiResponse<>("Delivery address was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Delivery address not found.", null, HttpStatus.NOT_FOUND.value());

            }

        } catch (Exception e) {
            log.error("An error occurred while deleting delivery address.", e);
            return new ApiResponse<>("An error occurred while deleting delivery address.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
