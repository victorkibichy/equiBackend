package com.EquiFarm.EquiFarm.OrderItem;

import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Farmer.FarmerRepository;
import com.EquiFarm.EquiFarm.Order.DTO.OrderResponse;
import com.EquiFarm.EquiFarm.Order.Order;
import com.EquiFarm.EquiFarm.Order.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.OrderItem.DTO.OrderItemResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

import org.modelmapper.ModelMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final FarmerRepository farmerRepository;
//    private final

    @Transactional
    public ApiResponse<?> getAllOrderItems() {
        try {
            List<OrderItem> orderItemList = orderItemRepository.findByDeletedFlag(Constants.NO);

            List<OrderItemResponse> orderItemResponses = orderItemList.stream()
                    .map(item -> modelMapper.map(item, OrderItemResponse.class))
                    .collect(Collectors.toList());
            return new ApiResponse<>("Farm Order Products were successfully fetched.", orderItemResponses,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching orders items.", e);

            return new ApiResponse<>("An error occurred while fetching order items.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getOrderItemById(Long id) {
        try {
            Optional<OrderItem> orderItemOptional = orderItemRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (orderItemOptional.isPresent()) {
                OrderItem orderItem = orderItemOptional.get();

                OrderItemResponse orderItemResponse = modelMapper.map(orderItem, OrderItemResponse.class);

                return new ApiResponse<>("Farm Order Item fetched successfully.", orderItemResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Farm Order Item not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching order.", e);

            return new ApiResponse<>("An error occurred while fetching order.", null,
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

            List<OrderItem> orderItems = orderItemRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            List<OrderItemResponse> orderItemResponses = orderItems.stream()
                    .map(item -> modelMapper.map(item, OrderItemResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("User Farm Order Products were successfully fetched.", orderItemResponses,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching user farm order item.", e);

            return new ApiResponse<>("An error occurred while fetching user farm order item.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getOrderItemsByUserId(Long id) {
        try {
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (userOptional.isPresent()) {
                List<OrderItem> orderItems = orderItemRepository.findByDeletedFlagAndUserId(Constants.NO,
                        id);

                List<OrderItemResponse> orderItemResponses = orderItems.stream()
                        .map(item -> modelMapper.map(item, OrderItemResponse.class))
                        .collect(Collectors.toList());

                return new ApiResponse<>("User Farm Order Products were successfully fetched.", orderItemResponses,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("User not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching user farm order item.", e);

            return new ApiResponse<>("An error occurred while fetching user farm order item.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
