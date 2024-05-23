package com.EquiFarm.EquiFarm.DeliveryAddress;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EquiFarm.EquiFarm.DeliveryAddress.DTO.DeliveryAddressRequest;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/deliveryAddresses")
@Tag(name = "Delivery Addresses")
@RequiredArgsConstructor
public class DeliveryAddressController {
    private final DeliveryAddressService deliveryAddressService;

    @PostMapping("/add")
    public ResponseEntity<?> addDeliveryAddress(@RequestBody DeliveryAddressRequest deliveryAddressRequest) {
        return ResponseEntity.ok(deliveryAddressService.addDeliveryAddress(deliveryAddressRequest));
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllDeliveryAddress() {
        return ResponseEntity.ok(deliveryAddressService.getAllDeliveryAddress());
    }

    @GetMapping("/get/by/getDeliveryAddressById/{deliveryAddressId}")
    public ResponseEntity<?> getAllDeliveryAddressById(@PathVariable("deliveryAddressId") Long id) {
        return ResponseEntity.ok(deliveryAddressService.getDeliveryAddressById(id));
    }

    @PutMapping("/update/{deliveryAddressId}")
    public ResponseEntity<?> updateDeliveryAddress(@RequestBody DeliveryAddressRequest deliveryAddressRequest,
            @PathVariable("deliveryAddressId") Long id) {
        return ResponseEntity.ok(deliveryAddressService.updateDeliveryAddress(deliveryAddressRequest, id));
    }

    @GetMapping("/get/byUser")
    public ResponseEntity<?> getUserDeliveryAddresses() {
        return ResponseEntity.ok(deliveryAddressService.getUserDeliveryAddresses());
    }

    @DeleteMapping("/delete/{deliveryAddressId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteDeliveryAddress(@PathVariable("deliveryAddressId") Long id) {
        return ResponseEntity.ok(deliveryAddressService.deleteDeliveryAddress(id));
    }

}
