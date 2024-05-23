package com.EquiFarm.EquiFarm.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.EquiFarm.EquiFarm.Customer.Customer;
import com.EquiFarm.EquiFarm.Customer.CustomerRepository;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Inventory.InventoryService;
import com.EquiFarm.EquiFarm.Farmer.FarmerRepository;
import com.sun.jdi.event.ExceptionEvent;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.apache.tomcat.util.codec.binary.Base64;

import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsRepository;
import com.EquiFarm.EquiFarm.Order.DTO.FarmProductOrderRequest;
import com.EquiFarm.EquiFarm.Order.DTO.OrderResponse;
import com.EquiFarm.EquiFarm.Order.DTO.RemoveFarmOrderItemRequest;
import com.EquiFarm.EquiFarm.OrderItem.OrderItem;
import com.EquiFarm.EquiFarm.OrderItem.OrderItemRepository;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FarmProductsRepository farmProductsRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;
    private final FarmerRepository farmerRepository;

    @Transactional
    public ApiResponse<?> addFarmProductToCart(FarmProductOrderRequest farmProductOrderRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            if (farmProductOrderRequest.getFarmProductId() == null) {
                return new ApiResponse<>("Product is required.", null, HttpStatus.BAD_REQUEST.value());
            }

            if (farmProductOrderRequest.getQuantity() == null) {
                return new ApiResponse<>("Product quantity is required.", null, HttpStatus.BAD_REQUEST.value());
            }

            Optional<FarmProducts> farmProductOptional = farmProductsRepository.findByDeletedFlagAndId(Constants.NO,
                    farmProductOrderRequest.getFarmProductId());

            if (farmProductOptional.isEmpty()) {
                return new ApiResponse<>("Product not found.", null, HttpStatus.NOT_FOUND.value());
            }

            FarmProducts product = farmProductOptional.get();

            if(currentUser.getId().equals(product.getFarmer().getUser().getId())){
                return new ApiResponse<>("You cannot purchase your own products.", null, HttpStatus.BAD_REQUEST.value());
            }

//            if (farmProductOrderRequest.getQuantity() > product.getUnitsAvailable()) {
//                return new ApiResponse<>("Quantity is higher than the available number of units.", null,
//                        HttpStatus.BAD_REQUEST.value());
//            }

            if(!inventoryService.productAvailable(product, farmProductOrderRequest.getQuantity())){
                return new ApiResponse<>("Quantity is higher than the available number of units", null, HttpStatus.BAD_REQUEST.value());
            }
            inventoryService.reserveUnits(product, farmProductOrderRequest.getQuantity());

            Optional<OrderItem> orderItemOptional = getOrderItemByProductAndUser(product.getId(), currentUser.getId());
            List<Order> orderList = getUnfinishedOrdersByUser(currentUser.getId());
            List<Order> orderFromFarmer = getOrderFromFarmer(orderList, product.getFarmer());

            if (!orderFromFarmer.isEmpty()) {
                Order order = orderFromFarmer.get(0);

                List<OrderItem> orderItemExistance = getOrderItemsByProductAndUser(order.getOrderItems(),
                        product.getId(), currentUser.getId());

                if (!orderItemExistance.isEmpty()) {
                    OrderItem orderItem = orderItemExistance.get(0);

                    // Remove order Item with quantity 0
                    if (farmProductOrderRequest.getQuantity() <= 0) {
                        System.out.println("Quantity::::> " + (farmProductOrderRequest.getQuantity() <= 0));
                        RemoveFarmOrderItemRequest request = new RemoveFarmOrderItemRequest();
                        List<Long> orderItemIds = new ArrayList<>();
                        orderItemIds.add(orderItem.getId());
                        request.setOrderItemIds(orderItemIds);

                        removeFarmProductsFromCart(request, order.getId());
                        // Remove empty order
                        if (order.getOrderItems().isEmpty()) {
                            markOrderAsDeleted(order, currentUser);
                            return new ApiResponse<>("Empty order was successfully removed.", null,
                                    HttpStatus.OK.value());
                        }

                        return new ApiResponse<>("Farm product was successfully removed.", null, HttpStatus.OK.value());
                    } else {
                        updateOrderItemAndOrder(orderItem, farmProductOrderRequest.getQuantity(), order);
                    }

                    OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
                    return new ApiResponse<>("Product quantity was successfully updated.", orderResponse,
                            HttpStatus.CREATED.value());
                } else {
                    // Check the quantity of the product before adding it to the order
                    if (farmProductOrderRequest.getQuantity() >= 1) {
                        OrderItem orderItem = createOrderItem(product, farmProductOrderRequest.getQuantity(),
                                currentUser);

                        Order updatedOrder = addOrderItemToOrder(order, orderItem, currentUser.getId());
                        OrderResponse orderResponse = modelMapper.map(updatedOrder, OrderResponse.class);
                        return new ApiResponse<>("Order item was successfully added to the cart.", orderResponse,
                                HttpStatus.CREATED.value());
                    } else {
                        return new ApiResponse<>("Quantity must be greater than 0.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                }
            } else {
                // Check the quantity of the first product is greater than 1 before creating a
                // new order
                if (farmProductOrderRequest.getQuantity() >= 1) {
                    OrderItem orderItem = orderItemOptional.orElseGet(OrderItem::new);
                    orderItem.setFarmProduct(product);
                    orderItem.setUnitOfMeasurements(product.getUnitOfMeasurements());
                    orderItem.setUser(currentUser);
                    orderItem.setQuantity(farmProductOrderRequest.getQuantity());
                    orderItemRepository.save(orderItem);

                    Order addedOrder = createNewOrder(orderItem,currentUser);
                    OrderResponse orderResponse = modelMapper.map(addedOrder, OrderResponse.class);
                    return new ApiResponse<>("Order was successfully created.", orderResponse,
                            HttpStatus.CREATED.value());
                } else {
                    return new ApiResponse<>("Order quantity must be greater than 0.", null,
                            HttpStatus.BAD_REQUEST.value());
                }

            }
        } catch (Exception e) {
            log.error("An error occurred while adding product to cart.", e);
            return new ApiResponse<>("An error occurred while adding product to cart.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    private Optional<OrderItem> getOrderItemByProductAndUser(Long productId, Long userId) {
        return orderItemRepository.findByDeletedFlagAndFarmProductIdAndUserIdAndIsPaidAndIsOrdered(
                Constants.NO, productId, userId, false, false);
    }
    private List<Order> getUnfinishedOrdersByUser(Long userId) {
        return orderRepository.findByDeletedFlagAndUserIdAndIsOrderedAndIsPaid(
                Constants.NO, userId, false, false);
    }
    public ApiResponse<?> fetchFarmerPendingOrders(){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null){
                return new ApiResponse<>("User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());
            if (farmerOptional.isEmpty()){
                return new ApiResponse<>("Farmer Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            Farmer farmer = farmerOptional.get();
            List<OrderStatus> orderStatuses = new ArrayList<>();
            orderStatuses.add(OrderStatus.IN_PROGRESS);
            orderStatuses.add(OrderStatus.CONFIRMED);
            List<Order> orderList = orderRepository.findByDeletedFlagAndOrderStatusIn(Constants.NO, orderStatuses);
            List<OrderResponse> pendingOrders = orderList.stream()
                    .filter(order -> (order.getOrderItems().get(0).getFarmProduct().getFarmer().equals(farmer)))
                    .map(checkout -> modelMapper.map(checkout, OrderResponse.class))
                    .toList();
            return new ApiResponse<>("Success fetching pending orders", pendingOrders, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error occurred", e);
            return new ApiResponse<>("An error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> fetchCustomerPendingOrders(Long userId){
        try {
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, userId);
            if (userOptional.isEmpty()){
                return new ApiResponse<>("User Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            User user = userOptional.get();
            List<Order> orderList = orderRepository.findByDeletedFlagAndOrderStatusAndIsOrderedAndIsPaid(Constants.NO, OrderStatus.IN_PROGRESS, true, false);
            List<OrderResponse> pendingOrders = orderList.stream()
                    .filter(order -> (order.getUser().equals(user)))
                    .map(checkout -> modelMapper.map(checkout, OrderResponse.class))
                    .toList();
            return new ApiResponse<>("Success fetching pending orders", pendingOrders, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error occurred", e);
            return new ApiResponse<>("An error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> acceptRequest(Long orderId){
        try {
            Optional<Order> orderOptional = orderRepository.findByDeletedFlagAndId(Constants.NO, orderId);
            if (orderOptional.isEmpty()){
                return new ApiResponse<>("Order not found", null, HttpStatus.NOT_FOUND.value());
            }
            Order order = orderOptional.get();
            order.setOrderStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            return new ApiResponse<>("Order Confirmed", null, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error occurred", e);
            return new ApiResponse<>("Error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> rejectRequest(Long orderId){
        try {
            Optional<Order> orderOptional = orderRepository.findByDeletedFlagAndId(Constants.NO, orderId);
            if (orderOptional.isEmpty()){
                return new ApiResponse<>("Order not found", null, HttpStatus.NOT_FOUND.value());
            }
            Order order = orderOptional.get();
            order.setOrderStatus(OrderStatus.REJECTED);
            orderRepository.save(order);
            return new ApiResponse<>("Order Confirmed", null, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error occurred", e);
            return new ApiResponse<>("Error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> fetchFarmerConfirmedOrders(){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if(currentUser == null){
                return new ApiResponse<>("User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, currentUser.getId());
            if(userOptional.isEmpty()){
                return new ApiResponse<>("Farmer not found", null, HttpStatus.NOT_FOUND.value());
            }
            User user = userOptional.get();
            List<OrderStatus> orderStatuses = new ArrayList<>();
            orderStatuses.add(OrderStatus.COMPLETED);
            orderStatuses.add(OrderStatus.REJECTED);
            orderStatuses.add(OrderStatus.CANCELLED);
            List<Order> orderList = orderRepository.findByDeletedFlagAndOrderStatusIn(Constants.NO, orderStatuses);
            List<OrderResponse> orderResponses = orderList
                    .stream()
                    .filter(order -> order.getOrderItems().get(0).getFarmProduct().getFarmer().getUser().equals(user))
                    .map(order-> modelMapper.map(order, OrderResponse.class))
                    .toList();
            return new ApiResponse<>("Orders fetched Successfully", orderResponses, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error occurred", e);
            return new ApiResponse<>("Error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> fetchCustomerConfirmedOrders(){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if(currentUser == null){
                return new ApiResponse<>("User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, currentUser.getId());
            if(userOptional.isEmpty()){
                return new ApiResponse<>("Customer not found", null, HttpStatus.NOT_FOUND.value());
            }
            User user = userOptional.get();
            List<OrderStatus> orderStatuses = new ArrayList<>();
            orderStatuses.add(OrderStatus.COMPLETED);
            orderStatuses.add(OrderStatus.REJECTED);
            orderStatuses.add(OrderStatus.CANCELLED);
            List<Order> orderList = orderRepository.findByDeletedFlagAndOrderStatusInAndIsOrderedAndIsPaid(Constants.NO, orderStatuses,true,false);
            List<OrderResponse> orderResponses = orderList
                    .stream()
                    .filter(order -> order.getUser().equals(user))
                    .map(order-> modelMapper.map(order, OrderResponse.class))
                    .toList();
            return new ApiResponse<>("Orders fetched Successfully", orderResponses, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error occurred", e);
            return new ApiResponse<>("Error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> fetchCustomerCompletedOrders( Long userId){
        try {
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, userId);
            if(userOptional.isEmpty()){
                return new ApiResponse<>("Customer not found", null, HttpStatus.NOT_FOUND.value());
            }
            User user = userOptional.get();
            List<OrderStatus> orderStatuses = new ArrayList<>();
            orderStatuses.add(OrderStatus.COMPLETED);
            orderStatuses.add(OrderStatus.REJECTED);
            orderStatuses.add(OrderStatus.CANCELLED);
            List<Order> orderList = orderRepository.findByDeletedFlagAndOrderStatusIn(Constants.NO, orderStatuses);
            List<OrderResponse> orderResponses = orderList
                    .stream()
                    .filter(order -> order.getUser().equals(user))
                    .map(order-> modelMapper.map(order, OrderResponse.class))
                    .toList();
            return new ApiResponse<>("Orders fetched Successfully", orderResponses, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error occurred", e);
            return new ApiResponse<>("Error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    private List<Order> getOrderFromFarmer(List<Order> orderList, Farmer farmer) {
        return orderList.stream()
                .filter(order -> order.getOrderItems().stream()
                        .allMatch(orderItem -> orderItem.getFarmProduct().getFarmer().equals(farmer)))
                .collect(Collectors.toList());
    }
    private List<OrderItem> getOrderItemsByProductAndUser(List<OrderItem> orderItems, Long productId, Long userId) {
        return orderItems.stream()
                .filter(orderItem -> orderItem.getFarmProduct().getId().equals(productId)
                        && orderItem.getIsOrdered() == false
                        && orderItem.getIsPaid() == false
                        && orderItem.getUser() != null
                        && orderItem.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }
    private void updateOrderItemAndOrder(OrderItem orderItem, Integer quantity, Order order) {
        orderItem.setQuantity(quantity);
        order.setOrderStatus(OrderStatus.PENDING);
        orderItemRepository.save(orderItem);
        order.setOrderId(generateOrderId(order.getUser().getId()));
        orderRepository.save(order);
    }
    private OrderItem createOrderItem(FarmProducts product, Integer quantity, User currentUser) {
        OrderItem orderItem = new OrderItem();
        orderItem.setFarmProduct(product);
        orderItem.setUnitOfMeasurements(product.getUnitOfMeasurements());
        orderItem.setQuantity(quantity);
        orderItem.setUser(currentUser);
        orderItemRepository.save(orderItem);
        return orderItem;
    }
    private Order addOrderItemToOrder(Order order, OrderItem orderItem, Long userId) {
        if (order.getOrderItems() == null) {
            order.setOrderItems(new ArrayList<>());
        }
        order.getOrderItems().add(orderItem);

        order.setOrderId(generateOrderId(userId));
        order.setOrderStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }
    private Order createNewOrder(OrderItem orderItem, User currentUser) {
        
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setUser(currentUser);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderItems(Collections.singletonList(orderItem));
        order.setOrderId(generateOrderId(currentUser.getId()));
        return orderRepository.save(order);
    }
    private String generateOrderId(Long userId) {
        byte[] userIdBytes = String.valueOf(userId).getBytes();
        String encodedBytes = Base64.encodeBase64String(userIdBytes);
        return new String(encodedBytes);
    }
    @Transactional
    public ApiResponse<?> getAllOrders() {
        try {
            List<Order> orderList = orderRepository.findByDeletedFlag(Constants.NO);

            List<OrderResponse> orderResponse = orderList.stream()
                    .map(order -> modelMapper.map(order, OrderResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Orders Fetched successfully.", orderResponse, HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching orders.", e);

            return new ApiResponse<>("An error occurred while fetching orders.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> getOrderById(Long id) {
        try {
            Optional<Order> orderOptional = orderRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);

                return new ApiResponse<OrderResponse>("Order Fetched Successfully.", orderResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Order not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching order.", e);

            return new ApiResponse<>("An error occurred while fetching order.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> userOrders() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            List<Order> ordersList = orderRepository.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());

            List<OrderResponse> orderResponse = ordersList.stream()
                    .map(order -> modelMapper.map(order, OrderResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Orders Fetched successfully.", orderResponse, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching user orders.", e);

            return new ApiResponse<>("An error occurred while fetching user orders.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> getUserOrdersByUserId(Long id) {
        try {
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (userOptional.isPresent()) {
                List<Order> ordersList = orderRepository.findByDeletedFlagAndUserId(Constants.NO, id);

                List<OrderResponse> orderResponse = ordersList.stream()
                        .map(order -> modelMapper.map(order, OrderResponse.class))
                        .collect(Collectors.toList());

                return new ApiResponse<>("Orders Fetched successfully.", orderResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("User not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching user orders.", e);

            return new ApiResponse<>("An error occurred while fetching user orders.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> removeFarmProductsFromCart(RemoveFarmOrderItemRequest removeFarmOrderItemRequest,
            Long orderId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Order> orderOptional = getOrderByIdAndCurrentUser(orderId, currentUser);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                List<Long> farmOrderItemsId = removeFarmOrderItemRequest.getOrderItemIds();

                if (farmOrderItemsId != null && !farmOrderItemsId.isEmpty()) {
                    removeFarmItemsFromOrder(order, farmOrderItemsId, currentUser);
                }

                if (order.getOrderItems().isEmpty()) {
                    markOrderAsDeleted(order, currentUser);
                    return new ApiResponse<>("Order was successfully removed.", null, HttpStatus.NO_CONTENT.value());
                } else {
                    Order updatedOrder = orderRepository.save(order);
                    OrderResponse orderResponse = modelMapper.map(updatedOrder, OrderResponse.class);
                    return new ApiResponse<>("Order was successfully updated.", orderResponse, HttpStatus.OK.value());
                }
            } else {
                return new ApiResponse<>("Order not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while removing a product from the cart.", e);
            return new ApiResponse<>("An error occurred while removing a product from the cart.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    private Optional<Order> getOrderByIdAndCurrentUser(Long orderId, User currentUser) {
        return orderRepository.findByDeletedFlagAndIdAndUserIdAndOrderStatusAndIsOrderedAndIsPaid(
                Constants.NO, orderId, currentUser.getId(), OrderStatus.PENDING, false, false);
    }
    private void removeFarmItemsFromOrder(Order order, List<Long> farmOrderItemsId, User currentUser) {
        List<OrderItem> farmItemsToRemove = order.getOrderItems().stream()
                .filter(item -> farmOrderItemsId.contains(item.getId())
                        && item.getDeletedFlag() == Constants.NO && !item.getIsPaid()
                        && !item.getIsOrdered())
                .collect(Collectors.toList());

        for (OrderItem item : farmItemsToRemove) {
            removeFarmProductFromOrderItem(item, currentUser);
        }

        order.getOrderItems().removeAll(farmItemsToRemove);
    }
    private void removeFarmProductFromOrderItem(OrderItem orderItem, User currentUser) {
        inventoryService.releaseReservedUnits(orderItem);
        orderItem.setFarmProduct(null);
        orderItem.setDeletedFlag(Constants.YES);
        orderItem.setDeletedBy(currentUser);
        orderItem.setDeletedAt(LocalDateTime.now());
        orderItem.setQuantity(0);
    }
    private void markOrderAsDeleted(Order order, User currentUser) {
        order.setDeletedFlag(Constants.YES);
        order.setDeletedBy(currentUser);
        order.setDeletedAt(LocalDateTime.now());
        order.setTotalAmount(0.00);
        orderRepository.save(order);
    }
}
