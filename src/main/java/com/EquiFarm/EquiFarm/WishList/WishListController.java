package com.EquiFarm.EquiFarm.WishList;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EquiFarm.EquiFarm.WishList.DTO.WishListRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/wishLists")
@Tag(name = "WishList")
@RequiredArgsConstructor
public class WishListController {
    private final WishListService wishListService;

    @GetMapping("/get")
    public ResponseEntity<?> getUserWishList() {
        return ResponseEntity.ok(wishListService.getUserWishList());
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllWishLists() {
        return ResponseEntity.ok(wishListService.getAllWishLists());
    }

    @PostMapping("/add/product")
    public ResponseEntity<?> addProductToWishList(@RequestBody WishListRequest wishListRequest) {
        return ResponseEntity.ok(wishListService.addProductToWishList(wishListRequest));
    }

    @PostMapping("/remove/products")
    public ResponseEntity<?> removeProductFromWishList(@RequestBody WishListRequest wishListRequest) {
        return ResponseEntity.ok(wishListService.removeProductFromWishList(wishListRequest));
    }


}
