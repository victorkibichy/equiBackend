package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessWishList;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessWishList.DTO.AgriBusinessWishListRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agriBusinessWishLists")
@Tag(name = "AgriBusinessWishList")
@RequiredArgsConstructor
public class AgriBusinessWishListController {
    private final AgriBusinessWishListService agriBusinessWishListService;

    @GetMapping("/get")
    public ResponseEntity<?> getUserAgriBusinessWishList() {
        return ResponseEntity.ok(agriBusinessWishListService.getUserAgriBusinessWishList());
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllWishLists() {
        return ResponseEntity.ok(agriBusinessWishListService.getAllAgriBusinessWishLists());
    }

    @PostMapping("/add/product")
    public ResponseEntity<?> addProductToAgriBusinesssWishList(
            @RequestBody AgriBusinessWishListRequest agriBusinessWishListRequest) {
        return ResponseEntity.ok(agriBusinessWishListService.addProductToAgriBusinessWishList(agriBusinessWishListRequest));
    }
    @PostMapping("/remove/products")
    public ResponseEntity<?> removeProductFromAgriBusinessWishList(
            @RequestBody AgriBusinessWishListRequest agriBusinessWishListRequest) {
        return ResponseEntity.ok(agriBusinessWishListService.removeProductFromAgriBusinessWishList(agriBusinessWishListRequest));
    }

}
