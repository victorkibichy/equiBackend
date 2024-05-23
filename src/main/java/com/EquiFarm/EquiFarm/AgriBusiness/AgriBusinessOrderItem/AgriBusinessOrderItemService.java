package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrderItem;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrderItem.DTO.AgriBusinessOrderItemResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgriBusinessOrderItemService {
    private final AgriBusinessOrderItemRepository agriBusinessOrderItemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> getAllAgriBusinessOrderItems() {
        try {
            List<AgriBusinessOrderItem> agriBusinessOrderItemList = agriBusinessOrderItemRepository.findByDeletedFlag(Constants.NO);

            List<AgriBusinessOrderItemResponse> agriBusinessOrderItemResponses = agriBusinessOrderItemList.stream()
                    .map(item -> modelMapper.map(item, AgriBusinessOrderItemResponse.class))
                    .collect(Collectors.toList());
            return new ApiResponse<>("AgriBusiness Order Products were successfully fetched.", agriBusinessOrderItemResponses,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching orders items.", e);

            return new ApiResponse<>("An error occurred while fetching order items.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAgriBusinessOrderItemById(Long id) {
        try {
            Optional<AgriBusinessOrderItem> agriBusinessOrderItemOptional = agriBusinessOrderItemRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (agriBusinessOrderItemOptional.isPresent()) {
                AgriBusinessOrderItem agriBusinessOrderItem = agriBusinessOrderItemOptional.get();

                AgriBusinessOrderItemResponse agriBusinessOrderItemResponse = modelMapper.map(agriBusinessOrderItem, AgriBusinessOrderItemResponse.class);

                return new ApiResponse<>("AgriBusiness Order Item fetched successfully.", agriBusinessOrderItem,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("AgriBusiness Order Item not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching order.", e);

            return new ApiResponse<>("An error occurred while fetching order.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAgriBusinessOrderItemsByUserId(Long id) {
        try {
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (userOptional.isPresent()) {
                List<AgriBusinessOrderItem> agriBusinessOrderItems = agriBusinessOrderItemRepository.findByDeletedFlagAndUserId(Constants.NO,
                        id);

                List<AgriBusinessOrderItemResponse> agriBusinessOrderItemResponses = agriBusinessOrderItems.stream()
                        .map(item -> modelMapper.map(item, AgriBusinessOrderItemResponse.class))
                        .collect(Collectors.toList());

                return new ApiResponse<>("User AgriBusiness Order Products were successfully fetched.", agriBusinessOrderItemResponses,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("User not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching user agribusiness order item.", e);

            return new ApiResponse<>("An error occurred while fetching user agribusiness order item.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getUserOrderItemsByUser() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            List<AgriBusinessOrderItem> agriBusinessOrderItems = agriBusinessOrderItemRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            List<AgriBusinessOrderItemResponse> agriBusinessOrderItemResponses = agriBusinessOrderItems.stream()
                    .map(item -> modelMapper.map(item, AgriBusinessOrderItemResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("User AgriBusiness Order Products were successfully fetched.", agriBusinessOrderItemResponses,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching user agribusiness order item.", e);

            return new ApiResponse<>("An error occurred while fetching user agribusiness order item.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
