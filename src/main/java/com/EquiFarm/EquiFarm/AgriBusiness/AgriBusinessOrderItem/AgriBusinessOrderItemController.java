package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessOrderItem;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agriBusinessOrderProducts")
@Tag(name = "AgriBusiness Order Items")
@RequiredArgsConstructor
public class AgriBusinessOrderItemController {
    private final AgriBusinessOrderItemService agriBusinessOrderItemService;

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllAgriBusinessOrderItems() {
        return ResponseEntity.ok(agriBusinessOrderItemService.getAllAgriBusinessOrderItems());
    }

    @GetMapping("/get/by/agriBusinessOrderItemId/{agriBusinessOrderItemId}")
    public ResponseEntity<?> getAgriBusinessOrderItemById(@PathVariable("agriBusinessOrderItemId") Long id) {
        return ResponseEntity.ok(agriBusinessOrderItemService.getAgriBusinessOrderItemById(id));
    }

    @GetMapping("/get/agriBusinessOrderItems/by/userId/{userId}")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAgriBusinessOrderItemsByUserId(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(agriBusinessOrderItemService.getAgriBusinessOrderItemsByUserId(id));
    }

    @GetMapping("/get/user/agriBusinessOrderProducts")
    public ResponseEntity<?> getUserAgriBusinessOrderItems() {
        return ResponseEntity.ok(agriBusinessOrderItemService.getUserOrderItemsByUser());
    }
}
