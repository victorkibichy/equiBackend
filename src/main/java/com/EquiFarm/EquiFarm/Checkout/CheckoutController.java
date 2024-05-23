package com.EquiFarm.EquiFarm.Checkout;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EquiFarm.EquiFarm.Checkout.DTO.CheckoutRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/checkOut")
@Tag(name = "Checkout")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllCheckouts() {
        return ResponseEntity.ok(checkoutService.getAllCheckout());
    }

    @GetMapping("/get/by/checkOutId/{checkOutId}")
    public ResponseEntity<?> getCheckoutById(@PathVariable("checkOutId") Long id) {
        return ResponseEntity.ok(checkoutService.getCheckoutById(id));
    }

    @GetMapping("/get/by/orderId/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable("orderId") Long id) {
        return ResponseEntity.ok(checkoutService.getCheckoutByOrderId(id));
    }

    @DeleteMapping("/delete/{checkoutId}")
    public ResponseEntity<?> deleteCheckoutById(@PathVariable("checkoutId") Long id) {
        return ResponseEntity.ok(checkoutService.deleteCheckout(id));
    }

    @PostMapping("/create/checkout")
    // @PreAuthorize("hasAuthority('customer:post')")
    public ResponseEntity<?> createCheckout(@RequestBody CheckoutRequest checkoutRequest) {
        return ResponseEntity.ok(checkoutService.createCheckout(checkoutRequest));
    }

}
