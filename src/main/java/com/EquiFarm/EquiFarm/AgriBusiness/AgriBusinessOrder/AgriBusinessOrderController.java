package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder.DTO.AgriBusinessOrderRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrder.DTO.RemoveAgriBusinessOrderItemRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agriBusinessOrders")
@Tag(name = "AgriBusiness Orders")
@RequiredArgsConstructor
public class AgriBusinessOrderController {
    private final AgriBusinessOrderService agriBusinessOrderService;

    @PostMapping("/add/agriBusinessProduct/to/cart")
    public ResponseEntity<?> addAgriBusinessProductToCart(@RequestBody AgriBusinessOrderRequest agriBusinessOrderRequest) {
        return ResponseEntity.ok(agriBusinessOrderService.addAgriBusinessProductToCart(agriBusinessOrderRequest));
    }

    @PostMapping("/cancel/agriBusinessOrder")
    public ResponseEntity<?> cancelAgriBusinessOrder(@RequestBody Long agriBusinessOrderId){
        return ResponseEntity.ok(agriBusinessOrderService.cancelAgriBusinessOrder(agriBusinessOrderId));
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllAgriBusinessOrders() {
        return ResponseEntity.ok(agriBusinessOrderService.getAllAgriBusinessOrders());
    }

    @GetMapping("/get/by/orderId/{agriBusinessOrderId}")
    public ResponseEntity<?> getAgriBusinessOrderByOrderId(@PathVariable("agriBusinessOrderId") Long id) {
        return ResponseEntity.ok(agriBusinessOrderService.getAgriBusinessOrderById(id));
    }

    @GetMapping("/get/all/customer/pending/agriBusinessOrders")
    public ResponseEntity<?> fetchCustomerPendingOrders() {
        return ResponseEntity.ok(agriBusinessOrderService.fetchPendingOrders());
    }
    @GetMapping("/get/all/agribusinessOwnercustomer/pending/agriBusinessOrders/{userId}")
    @PreAuthorize("hasAuthority('agriBusinessOwner:read')")
    public ResponseEntity<?> fetchPendingAgriBusinessOrdersByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(agriBusinessOrderService.fetchPendingAgriBusinessOrdersByUserId(userId));
    }

    @GetMapping("/get/all/customer/complete/orders/{userId}")
    public ResponseEntity<?> getAllCompleteOrders(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(agriBusinessOrderService.fetchCustomerCompletedAgriBusinessOrders(userId));
    }

    @GetMapping("/get/user/orders")
    public ResponseEntity<?> getAgriBusinessUserOrders() {
        return ResponseEntity.ok(agriBusinessOrderService.agriBusinessUserOrders());
    }

    @GetMapping("/get/by/userId/{userId}")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAgriBusinessOrdersByUserId(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(agriBusinessOrderService.getAgriBusinessUserOrdersByUserId(id));
    }

    @PostMapping("/remove/agriBusinessOrderItem/from/cart/{orderId}")
    public ResponseEntity<?> removeAagriBusinessOrderItemFromCart(@PathVariable("orderId") Long id,
                                                         @RequestBody RemoveAgriBusinessOrderItemRequest removeAgriBusinessOrderItemRequest) {
        return ResponseEntity.ok(agriBusinessOrderService.removeAgriBusinessProductsFromCart(removeAgriBusinessOrderItemRequest, id));
    }

}
