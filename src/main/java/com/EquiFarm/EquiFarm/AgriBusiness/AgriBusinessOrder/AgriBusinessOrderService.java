package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusiness;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder.DTO.AgriBusinessOrderRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder.DTO.AgriBusinessOrderResponse;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder.DTO.RemoveAgriBusinessOrderItemRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrderItem.AgriBusinessOrderItem;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrderItem.AgriBusinessOrderItemRepository;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProduct;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductRepository;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.Inventory.AgriInventoryService;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessRepository;
import com.EquiFarm.EquiFarm.Order.DTO.OrderResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AgriBusinessOrderService {

    private final AgriBusinessOrderRepository agriBusinessOrderRepository;
    private final AgriBusinessOrderItemRepository agriBusinessOrderItemRepository;
    private final AgriBusinessProductRepository agriBusinessProductRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AgriInventoryService agriInventoryService;
    private final AgriBusinessRepository agriBusinessRepository;

    public ApiResponse<?> addAgriBusinessProductToCart(AgriBusinessOrderRequest agriBusinessOrderRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            if (agriBusinessOrderRequest.getAgriBusinessProductId() == null) {
                return new ApiResponse<>("Product is required.", null, HttpStatus.BAD_REQUEST.value());
            }

            if (agriBusinessOrderRequest.getQuantity() == null) {
                return new ApiResponse<>("Product quantity is required.", null, HttpStatus.BAD_REQUEST.value());
            }

            Optional<AgriBusinessProduct> agriBusinessProductOptional = agriBusinessProductRepository.findByDeletedFlagAndId(Constants.NO,
                    agriBusinessOrderRequest.getAgriBusinessProductId());

            if (agriBusinessProductOptional.isEmpty()) {
                return new ApiResponse<>("Product not found.", null, HttpStatus.NOT_FOUND.value());
            }

            AgriBusinessProduct agriBusinessProduct = agriBusinessProductOptional.get();

//            if (agriBusinessOrderRequest.getQuantity() > agriBusinessProduct.getUnitsAvailable()) {
//                return new ApiResponse<>("Quantity is higher than the available number of units.", null,
//                        HttpStatus.BAD_REQUEST.value());
//            }

            if(!agriInventoryService.productAvailable(agriBusinessProduct, agriBusinessOrderRequest.getQuantity())){
                return new ApiResponse<>("Quantity is higher than the available number of units", null, HttpStatus.BAD_REQUEST.value());
            }
            agriInventoryService.reserveUnits(agriBusinessProduct, agriBusinessOrderRequest.getQuantity());

            Optional<AgriBusinessOrderItem> agriBusinessOrderItemOptional = getAgriBusinessOrderItemByProductAndUser(agriBusinessProduct.getId(), currentUser.getId());
            List<AgriBusinessOrder> agriBusinessOrderList = getUnfinishedAgriBusinessOrdersByUser(currentUser.getId());
            List<AgriBusinessOrder> orderFromAgriBusiness = getOrderFromAgriBusiness(agriBusinessOrderList, agriBusinessProduct.getAgriBusiness());

            if (!orderFromAgriBusiness.isEmpty()) {
                AgriBusinessOrder agriBusinessOrder = orderFromAgriBusiness.get(0);

                List<AgriBusinessOrderItem> agriBusinessOrderItemExistance = getAgriBusinessOrderItemsByProductAndUser(agriBusinessOrder.getAgriBusinessOrderItems(),
                        agriBusinessProduct.getId(), currentUser.getId());

                if (!agriBusinessOrderItemExistance.isEmpty()) {
                    AgriBusinessOrderItem agriBusinessOrderItem = agriBusinessOrderItemExistance.get(0);

                    // Remove order Item with quantity 0
                    if (agriBusinessOrderRequest.getQuantity() <= 0) {
                        System.out.println("Quantity::::> " + (agriBusinessOrderRequest.getQuantity() <= 0));
                        RemoveAgriBusinessOrderItemRequest request = new RemoveAgriBusinessOrderItemRequest();
                        List<Long> agriBusinessOrderItemsIds = new ArrayList<>();
                        agriBusinessOrderItemsIds.add(agriBusinessOrderItem.getId());
                        request.setAgriBusinessOrderItemsIds(agriBusinessOrderItemsIds);

                        removeAgriBusinessProductsFromCart(request, agriBusinessOrder.getId());
                        // Remove empty order
                        if (agriBusinessOrder.getAgriBusinessOrderItems().isEmpty()) {
                            markAgriBusinessOrderAsDeleted(agriBusinessOrder, currentUser);
                            return new ApiResponse<>("Empty order was successfully removed.", null,
                                    HttpStatus.OK.value());
                        }

                        return new ApiResponse<>("AgriBusiness product was successfully removed.", null, HttpStatus.OK.value());
                    } else {
                        updateAgriBusinessOrderItemAndAgriBusinessOrder(agriBusinessOrderItem, agriBusinessOrderRequest.getQuantity(), agriBusinessOrder);
                    }

                    AgriBusinessOrderResponse agriBusinessOrderResponse = modelMapper.map(agriBusinessOrder, AgriBusinessOrderResponse.class);
                    return new ApiResponse<>("AgriBusiness product quantity was successfully updated.", agriBusinessOrderResponse,
                            HttpStatus.CREATED.value());
                } else {
                    // Check the quantity of the product before adding it to the order
                    if (agriBusinessOrderRequest.getQuantity() >= 1) {
                        AgriBusinessOrderItem agriBusinessOrderItem = createAgriBusinessOrderItem(agriBusinessProduct, agriBusinessOrderRequest.getQuantity(),
                                currentUser);

                        AgriBusinessOrder updatedAgriBusinessOrder = addAgriBusinessOrderItemToOrder(agriBusinessOrder, agriBusinessOrderItem, currentUser.getId());
                        AgriBusinessOrderResponse agriBusinessOrderResponse = modelMapper.map(updatedAgriBusinessOrder, AgriBusinessOrderResponse.class);
                        return new ApiResponse<>(" Agribusiness order item was successfully added to the cart.", agriBusinessOrderResponse,
                                HttpStatus.CREATED.value());
                    } else {
                        return new ApiResponse<>("Quantity must be greater than 0.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                }
            } else {
                // Check the quantity of the first product is greater than 1 before creating a
                // new order
                if (agriBusinessOrderRequest.getQuantity() >= 1) {
                    AgriBusinessOrderItem agriBusinessOrderItem = agriBusinessOrderItemOptional.orElseGet(AgriBusinessOrderItem::new);
                    agriBusinessOrderItem.setAgriBusinessProduct(agriBusinessProduct);
                    agriBusinessOrderItem.setUnitOfMeasurements(agriBusinessProduct.getUnitOfMeasurements());
                    agriBusinessOrderItem.setUser(currentUser);
                    agriBusinessOrderItem.setQuantity(agriBusinessOrderRequest.getQuantity());
                    agriBusinessOrderItemRepository.save(agriBusinessOrderItem);

                    AgriBusinessOrder addedAgriBusinessOrder = createNewAgriBusinessOrder(agriBusinessOrderItem,currentUser);
                    AgriBusinessOrderResponse agriBusinessOrderResponse = modelMapper.map(addedAgriBusinessOrder, AgriBusinessOrderResponse.class);
                    return new ApiResponse<>("Agribusiness order was successfully created.", agriBusinessOrderResponse,
                            HttpStatus.CREATED.value());
                } else {
                    return new ApiResponse<>("Agribusiness order quantity must be greater than 0.", null,
                            HttpStatus.BAD_REQUEST.value());
                }

            }
        } catch (Exception e) {
            log.error("An error occurred while adding product to cart.", e);
            return new ApiResponse<>("An error occurred while adding product to cart.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> cancelAgriBusinessOrder(Long orderId){
        try {
            Optional<AgriBusinessOrder> agriBusinessOrderOptional = agriBusinessOrderRepository
                    .findByDeletedFlagAndId(Constants.NO, orderId);

            if (agriBusinessOrderOptional.isEmpty()){
                return new ApiResponse<>("Order not found", null, HttpStatus.NOT_FOUND.value());
            }

            AgriBusinessOrder agriBusinessOrder = agriBusinessOrderOptional.get();
            agriBusinessOrder.setAgriBusinessOrderStatus(AgriBusinessOrderStatus.CANCELLED);
            agriBusinessOrderRepository.save(agriBusinessOrder);

            return new ApiResponse<>("Order cancelled,", null, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error occurred", e);
            return new ApiResponse<>("Error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private Optional<AgriBusinessOrderItem> getAgriBusinessOrderItemByProductAndUser(Long productId, Long userId) {
        return agriBusinessOrderItemRepository.findByDeletedFlagAndAgriBusinessProductIdAndUserIdAndIsPaidAndIsOrdered(
                Constants.NO, productId, userId, false, false);
    }

    private List<AgriBusinessOrder> getUnfinishedAgriBusinessOrdersByUser(Long userId) {
        return agriBusinessOrderRepository.findByDeletedFlagAndUserIdAndIsOrderedAndIsPaid(
                Constants.NO, userId, false, false);
    }

    public ApiResponse<?> fetchPendingOrders(){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null){
                return new ApiResponse<>("User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, currentUser.getId());
            if (userOptional.isEmpty()){
                return new ApiResponse<>("User Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            User user = userOptional.get();
            List<AgriBusinessOrderStatus> agriBusinessOrderStatuses = new ArrayList<>();
            agriBusinessOrderStatuses.add(AgriBusinessOrderStatus.IN_PROGRESS);
            agriBusinessOrderStatuses.add(AgriBusinessOrderStatus.PENDING);
//            agriBusinessOrderStatuses.add(OrderStatus.CONFIRMED);

            List<AgriBusinessOrder> agriBusinessOrderList = agriBusinessOrderRepository
                    .findByDeletedFlagAndAgriBusinessOrderStatusIn(Constants.NO, agriBusinessOrderStatuses);

            List<AgriBusinessOrderResponse> pendingAgriBusinessOrders = agriBusinessOrderList.stream()
                    .filter(agriBusinessOrder -> (agriBusinessOrder.getUser().equals(user)))
                    .map(agriBusinessCheckout -> modelMapper.map(agriBusinessCheckout, AgriBusinessOrderResponse.class))
                    .toList();

            return new ApiResponse<>("Success fetching pending orders", pendingAgriBusinessOrders, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error occurred", e);
            return new ApiResponse<>("An error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> fetchPendingAgriBusinessOrdersByUserId(Long userId){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null){
                return new ApiResponse<>("User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<AgriBusiness> agriBusinessOptional = agriBusinessRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());
            if (agriBusinessOptional.isEmpty()){
                return new ApiResponse<>("AgriBusiness Owner Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            AgriBusiness agriBusiness = agriBusinessOptional.get();


            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, userId);
            if (userOptional.isEmpty()){
                return new ApiResponse<>("User Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            User user = userOptional.get();

            List<AgriBusinessOrderStatus> agriBusinessOrderStatuses = new ArrayList<>();
            agriBusinessOrderStatuses.add(AgriBusinessOrderStatus.IN_PROGRESS);
            agriBusinessOrderStatuses.add(AgriBusinessOrderStatus.PENDING);
//            agriBusinessOrderStatuses.add(OrderStatus.CONFIRMED);

            List<AgriBusinessOrder> agriBusinessOrderList = agriBusinessOrderRepository
                    .findByDeletedFlagAndAgriBusinessOrderStatusIn(Constants.NO, agriBusinessOrderStatuses);

            List<AgriBusinessOrderResponse> pendingAgriBusinessOrders = agriBusinessOrderList.stream()
                    .filter(agriBusinessOrder -> (agriBusinessOrder.getAgriBusinessOrderItems().get(0)
                            .getAgriBusinessProduct().getAgriBusiness().equals(agriBusiness)))

                    .filter(agriBusinessOrder -> (agriBusinessOrder.getUser().equals(user)))

                    .map(agriBusinessCheckout -> modelMapper.map(agriBusinessCheckout, AgriBusinessOrderResponse.class))
                    .toList();
            return new ApiResponse<>("Success fetching pending orders", pendingAgriBusinessOrders, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error occurred", e);
            return new ApiResponse<>("An error occurred", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public  ApiResponse<?>  fetchCustomerCompletedAgriBusinessOrders(Long userId){
        try {
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, userId);
            if(userOptional.isEmpty()){
                return new ApiResponse<>("Customer not found", null, HttpStatus.NOT_FOUND.value());
            }
            User user = userOptional.get();

            List<AgriBusinessOrderStatus> agriBusinessOrderStatuses = new ArrayList<>();
            agriBusinessOrderStatuses.add(AgriBusinessOrderStatus.COMPLETED);
//            agriBusinessOrderStatuses.add(AgriBusinessOrderStatus.REJECTED);
            agriBusinessOrderStatuses.add(AgriBusinessOrderStatus.CANCELLED);

            List<AgriBusinessOrder> agriBusinessOrderList = agriBusinessOrderRepository.
                    findByDeletedFlagAndAgriBusinessOrderStatusIn(Constants.NO, agriBusinessOrderStatuses);
            List<AgriBusinessOrderResponse> agriBusinessOrderResponses = agriBusinessOrderList.stream().
                    filter(agriBusinessOrder -> agriBusinessOrder.getUser().equals(user))
                    .map(agriBusinessOrder -> modelMapper.map(agriBusinessOrder, AgriBusinessOrderResponse.class))
                    .toList();
            return new ApiResponse<>("AgriBusiness orders fetched successfully",
                    agriBusinessOrderResponses, HttpStatus.OK.value());

        }catch (Exception e){
            log.error("Error occurred", e);
            return new ApiResponse<>("An error occurred while fetching agriBusiness orders", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private List<AgriBusinessOrder> getOrderFromAgriBusiness(List<AgriBusinessOrder> agriBusinessOrderList, AgriBusiness agriBusiness) {
        return agriBusinessOrderList.stream()
                .filter(agriBusinessOrder -> agriBusinessOrder.getAgriBusinessOrderItems().stream()
                        .allMatch(agriBusinessOrderItem -> agriBusinessOrderItem.getAgriBusinessProduct().getAgriBusiness().equals(agriBusiness)))
                .collect(Collectors.toList());
    }

    private List<AgriBusinessOrderItem> getAgriBusinessOrderItemsByProductAndUser(List<AgriBusinessOrderItem> agriBusinessOrderItems, Long productId, Long userId) {
        return agriBusinessOrderItems.stream()
                .filter(agriBusinessOrderItem -> agriBusinessOrderItem.getAgriBusinessProduct().getId().equals(productId)
                        && agriBusinessOrderItem.getIsOrdered() == false
                        && agriBusinessOrderItem.getIsPaid() == false
                        && agriBusinessOrderItem.getUser() != null
                        && agriBusinessOrderItem.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }
    private void updateAgriBusinessOrderItemAndOrder(AgriBusinessOrderItem agriBusinessOrderItem, Integer quantity, AgriBusinessOrder agriBusinessOrder) {
        agriBusinessOrderItem.setQuantity(quantity);
        agriBusinessOrder.setAgriBusinessOrderStatus(AgriBusinessOrderStatus.PENDING);
        agriBusinessOrderItemRepository.save(agriBusinessOrderItem);
        agriBusinessOrder.setAgriBusinessOrderId(generateAgriBusinessOrderId(agriBusinessOrder.getUser().getId()));
        agriBusinessOrderRepository.save(agriBusinessOrder);
    }

    private AgriBusinessOrderItem createAgriBusinessOrderItem(AgriBusinessProduct agriBusinessProduct, Integer quantity, User currentUser) {
        AgriBusinessOrderItem agriBusinessOrderItem = new AgriBusinessOrderItem();
        agriBusinessOrderItem.setAgriBusinessProduct(agriBusinessProduct);
        agriBusinessOrderItem.setUnitOfMeasurements(agriBusinessProduct.getUnitOfMeasurements());
        agriBusinessOrderItem.setQuantity(quantity);
        agriBusinessOrderItem.setUser(currentUser);
        agriBusinessOrderItemRepository.save(agriBusinessOrderItem);
        return agriBusinessOrderItem;
    }

    private AgriBusinessOrder addAgriBusinessOrderItemToOrder(AgriBusinessOrder agriBusinessOrder, AgriBusinessOrderItem agriBusinessOrderItem, Long userId) {
        if (agriBusinessOrder.getAgriBusinessOrderItems() == null) {
            agriBusinessOrder.setAgriBusinessOrderItems(new ArrayList<>());
        }
        agriBusinessOrder.getAgriBusinessOrderItems().add(agriBusinessOrderItem);

        agriBusinessOrder.setAgriBusinessOrderId(generateAgriBusinessOrderId(userId));
        agriBusinessOrder.setAgriBusinessOrderStatus(AgriBusinessOrderStatus.PENDING);
        return agriBusinessOrderRepository.save(agriBusinessOrder);
    }

    private AgriBusinessOrder createNewAgriBusinessOrder(AgriBusinessOrderItem agriBusinessOrderItem, User currentUser) {

        AgriBusinessOrder agriBusinessOrder = new AgriBusinessOrder();
        agriBusinessOrder.setOrderDate(LocalDateTime.now());
        agriBusinessOrder.setUser(currentUser);
        agriBusinessOrder.setAgriBusinessOrderStatus(AgriBusinessOrderStatus.PENDING);
        agriBusinessOrder.setAgriBusinessOrderItems(Collections.singletonList(agriBusinessOrderItem));
        agriBusinessOrder.setAgriBusinessOrderId(generateAgriBusinessOrderId(currentUser.getId()));
        return agriBusinessOrderRepository.save(agriBusinessOrder);
    }

    private String generateAgriBusinessOrderId(Long userId) {
        byte[] userIdBytes = String.valueOf(userId).getBytes();
        String encodedBytes = Base64.encodeBase64String(userIdBytes);
        return new String(encodedBytes);
    }
    @Transactional
    public ApiResponse<?> getAllAgriBusinessOrders() {
        try {
            List<AgriBusinessOrder> agriBusinessOrderList = agriBusinessOrderRepository.findByDeletedFlag(Constants.NO);

            List<AgriBusinessOrderResponse> agriBusinessOrderResponses = agriBusinessOrderList.stream()
                    .map(agriBusinessOrder -> modelMapper.map(agriBusinessOrder, AgriBusinessOrderResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("AgriBusiness Orders Fetched successfully.", agriBusinessOrderResponses, HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching agribusiness orders.", e);

            return new ApiResponse<>("An error occurred while fetching agribusiness orders.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> agriBusinessUserOrders() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            List<AgriBusinessOrder> agriBusinessOrderList = agriBusinessOrderRepository.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());

            List<AgriBusinessOrderResponse> agriBusinessOrderResponses = agriBusinessOrderList.stream()
                    .map(agriBusinessOrder -> modelMapper.map(agriBusinessOrder, AgriBusinessOrderResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Orders Fetched successfully.", agriBusinessOrderResponses, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching user orders.", e);

            return new ApiResponse<>("An error occurred while fetching user orders.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> getAgriBusinessOrderById(Long id) {
        try {
            Optional<AgriBusinessOrder> agriBusinessOrderOptional = agriBusinessOrderRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (agriBusinessOrderOptional.isPresent()) {
                AgriBusinessOrder agriBusinessOrder = agriBusinessOrderOptional.get();
                AgriBusinessOrderResponse agriBusinessOrderResponse = modelMapper.map(agriBusinessOrder, AgriBusinessOrderResponse.class);

                return new ApiResponse<AgriBusinessOrderResponse>("AgriBusiness order Fetched Successfully.", agriBusinessOrderResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("AgriBusiness order not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching agribusiness order.", e);

            return new ApiResponse<>("An error occurred while fetching agribusiness order.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    @Transactional
    public ApiResponse<?> getAgriBusinessUserOrdersByUserId(Long id) {
        try {
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (userOptional.isPresent()) {
                List<AgriBusinessOrder> agriBusinessOrderList = agriBusinessOrderRepository.findByDeletedFlagAndUserId(Constants.NO, id);

                List<AgriBusinessOrderResponse> agriBusinessOrderResponses = agriBusinessOrderList.stream()
                        .map(agriBusinessOrder -> modelMapper.map(agriBusinessOrder, AgriBusinessOrderResponse.class))
                        .collect(Collectors.toList());

                return new ApiResponse<>("Orders Fetched successfully.", agriBusinessOrderResponses, HttpStatus.OK.value());
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
    public ApiResponse<?> removeAgriBusinessProductsFromCart(RemoveAgriBusinessOrderItemRequest removeAgriBusinessOrderItemRequest,
                                                     Long orderId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<AgriBusinessOrder> agriBusinessOrderOptional = getAgriBusinessOrderByIdAndCurrentUser(orderId, currentUser);
            if (agriBusinessOrderOptional.isPresent()) {
                AgriBusinessOrder agriBusinessOrder = agriBusinessOrderOptional.get();
                List<Long> agriBusinessOrderItemsId = removeAgriBusinessOrderItemRequest.getAgriBusinessOrderItemsIds();

                if (agriBusinessOrderItemsId != null && !agriBusinessOrderItemsId.isEmpty()) {
                    removeAgriBusinessItemsFromOrder(agriBusinessOrder, agriBusinessOrderItemsId, currentUser);
                }

                if (agriBusinessOrder.getAgriBusinessOrderItems().isEmpty()) {
                    markAgriBusinessOrderAsDeleted(agriBusinessOrder, currentUser);
                    return new ApiResponse<>("Order was successfully removed.", null, HttpStatus.NO_CONTENT.value());
                } else {
                    AgriBusinessOrder updatedOrder = agriBusinessOrderRepository.save(agriBusinessOrder);
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
    private Optional<AgriBusinessOrder> getAgriBusinessOrderByIdAndCurrentUser(Long orderId, User currentUser) {
        return agriBusinessOrderRepository.findByDeletedFlagAndIdAndUserIdAndAgriBusinessOrderStatusAndIsOrderedAndIsPaid(
                Constants.NO, orderId, currentUser.getId(), AgriBusinessOrderStatus.PENDING, false, false);
    }
    private void markAgriBusinessOrderAsDeleted(AgriBusinessOrder agriBusinessOrder, User currentUser) {
        agriBusinessOrder.setDeletedFlag(Constants.YES);
        agriBusinessOrder.setDeletedBy(currentUser);
        agriBusinessOrder.setDeletedAt(LocalDateTime.now());
        agriBusinessOrder.setTotalAmount(0.00);
        agriBusinessOrderRepository.save(agriBusinessOrder);
    }
    private void updateAgriBusinessOrderItemAndAgriBusinessOrder(AgriBusinessOrderItem agriBusinessOrderItem, Integer quantity, AgriBusinessOrder agriBusinessOrder) {
        agriBusinessOrderItem.setQuantity(quantity);
        agriBusinessOrder.setAgriBusinessOrderStatus(AgriBusinessOrderStatus.PENDING);
        agriBusinessOrderItemRepository.save(agriBusinessOrderItem);
        agriBusinessOrder.setAgriBusinessOrderId(generateAgriBusinessOrderId(agriBusinessOrder.getUser().getId()));
        agriBusinessOrderRepository.save(agriBusinessOrder);
    }
    private void removeAgriBusinessItemsFromOrder(AgriBusinessOrder agriBusinessOrder, List<Long> agriBusinessOrderItemsId, User currentUser) {
//        agriInventoryService.releaseReservedUnits(agriBusinessOrder );
        List<AgriBusinessOrderItem> agriBusinessItemsToRemove = agriBusinessOrder.getAgriBusinessOrderItems().stream()
                .filter(item -> agriBusinessOrderItemsId.contains(item.getId())
                        && item.getDeletedFlag() == Constants.NO && !item.getIsPaid()
                        && !item.getIsOrdered())
                .collect(Collectors.toList());

        for (AgriBusinessOrderItem item : agriBusinessItemsToRemove) {
            removeAgriBusinessProductFromOrderItem(item, currentUser);
        }

        agriBusinessOrder.getAgriBusinessOrderItems().removeAll(agriBusinessItemsToRemove);
    }
    private void removeAgriBusinessProductFromOrderItem(AgriBusinessOrderItem agriBusinessOrderItem, User currentUser) {
        agriBusinessOrderItem.setAgriBusinessProduct(null);
        agriBusinessOrderItem.setDeletedFlag(Constants.YES);
        agriBusinessOrderItem.setDeletedBy(currentUser);
        agriBusinessOrderItem.setDeletedAt(LocalDateTime.now());
        agriBusinessOrderItem.setQuantity(0);
    }







}
