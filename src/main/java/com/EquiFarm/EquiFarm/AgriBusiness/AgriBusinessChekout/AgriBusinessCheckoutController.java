package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessChekout;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessChekout.DTO.AgriBusinessCheckoutRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agriBusinessCheckOut")
@Tag(name = "AgriBusiness Checkout")
@RequiredArgsConstructor
public class AgriBusinessCheckoutController {
    private final AgriBusinessCheckoutService agriBusinessCheckoutService;

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllAgriBusinessCheckouts() {
        return ResponseEntity.ok(agriBusinessCheckoutService.getAllAgriBusinessCheckout());
    }

    @GetMapping("/get/by/agriBusinessCheckoutId/{agriBusinessCheckoutId}")
    public ResponseEntity<?> getAgriBusinessCheckoutById(@PathVariable("agriBusinessCheckoutId") Long id) {
        return ResponseEntity.ok(agriBusinessCheckoutService.getAgriBusinessCheckoutById(id));
    }

    @GetMapping("/get/by/agriBusinessOrderId/{agriBusinessOrderId}")
    public ResponseEntity<?> getAgriBusinessOrderById(@PathVariable("agriBusinessOrderId") Long id) {
        return ResponseEntity.ok(agriBusinessCheckoutService.getAgriBusinessCheckoutByAgriBusinessOrderId(id));
    }

    @DeleteMapping("/delete/{agriBusinessCheckoutId}")
    public ResponseEntity<?> deleteCheckoutById(@PathVariable("agriBusinessCheckoutId") Long id) {
        return ResponseEntity.ok(agriBusinessCheckoutService.deleteAgriBusinessCheckout(id));
    }

    @PostMapping("/create/agriBusinessCheckout")
    public ResponseEntity<?> createAgriBusinessCheckout(
            @RequestBody AgriBusinessCheckoutRequest agriBusinessCheckoutRequest) {
        return ResponseEntity.ok(agriBusinessCheckoutService.createAgriBusinessCheckout(agriBusinessCheckoutRequest));
    }
}
