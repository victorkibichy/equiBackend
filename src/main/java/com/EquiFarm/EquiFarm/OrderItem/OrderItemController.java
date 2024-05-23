package com.EquiFarm.EquiFarm.OrderItem;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/farmOrderProducts")
@Tag(name = "Farm Order Products")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllFarmProducts() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    @GetMapping("/get/by/farmOrderProductId/{farmOrderProductId}")
    public ResponseEntity<?> getOrderItemById(@PathVariable("farmOrderProductId") Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    @GetMapping("/get/user/farmOrderProducts")
    public ResponseEntity<?> getUserFarmOrderItems() {
        return ResponseEntity.ok(orderItemService.getUserOrderItemsByUser());
    }


    @GetMapping("/get/farmOrderProducts/by/userId/{userId}")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getFarmOrderItemsByUserId(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByUserId(id));
    }

}
