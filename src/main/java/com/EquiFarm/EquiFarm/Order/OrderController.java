package com.EquiFarm.EquiFarm.Order;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EquiFarm.EquiFarm.Order.DTO.FarmProductOrderRequest;
import com.EquiFarm.EquiFarm.Order.DTO.RemoveFarmOrderItemRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Farm Orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/add/farmProduct/to/cart")
    public ResponseEntity<?> addFarmProductToCart(@RequestBody FarmProductOrderRequest farmProductOrderRequest) {
        return ResponseEntity.ok(orderService.addFarmProductToCart(farmProductOrderRequest));
    }

    @GetMapping("/get/all")
//    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/get/by/orderId/{orderId}")
    public ResponseEntity<?> getOrderByOrderId(@PathVariable("orderId") Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }


    @GetMapping("/get/all/farmer/pending/orders")
    @PreAuthorize("hasAuthority('farmer:read')")
    public ResponseEntity<?> getAllFarmerPendingOrders() {
        return ResponseEntity.ok(orderService.fetchFarmerPendingOrders());
    }
    @GetMapping("/get/all/customer/pending/orders/{userId}")
    public ResponseEntity<?> getFarmerOtherOrders(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(orderService.fetchCustomerPendingOrders(userId));
    }

    @GetMapping("/get/all/farmer/complete/orders")
    @PreAuthorize("hasAuthority('farmer:read')")
    public ResponseEntity<?> getCustomerOtherOrders() {
        return ResponseEntity.ok(orderService.fetchFarmerConfirmedOrders());
    }
    @GetMapping("/get/all/customer/complete/orders/{userId}")
    public ResponseEntity<?> getAllPendingOrders(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(orderService.fetchCustomerCompletedOrders(userId));
    }

    @PostMapping("/accept/order")
    @PreAuthorize("hasAuthority('farmer:update')")
    public ResponseEntity<?> acceptOrder(@RequestBody Long orderId){
        return ResponseEntity.ok(orderService.acceptRequest(orderId));
    }

    @PostMapping("/reject/order")
    @PreAuthorize("hasAuthority('farmer:update')")
    public ResponseEntity<?> rejectOrder(@RequestBody Long orderId){
        return ResponseEntity.ok(orderService.rejectRequest(orderId));
    }

    @GetMapping("/get/user/orders")
    public ResponseEntity<?> getUserOrders() {
        return ResponseEntity.ok(orderService.userOrders());
    }

    @GetMapping("/get/by/userId/{userId}")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(orderService.getUserOrdersByUserId(id));
    }

    @PostMapping("/remove/farmOrderItem/from/cart/{orderId}")
    public ResponseEntity<?> removeFarmOrderItemFromCart(@PathVariable("orderId") Long id,
            @RequestBody RemoveFarmOrderItemRequest removeFarmOrderItemRequest) {
        return ResponseEntity.ok(orderService.removeFarmProductsFromCart(removeFarmOrderItemRequest, id));
    }

}
