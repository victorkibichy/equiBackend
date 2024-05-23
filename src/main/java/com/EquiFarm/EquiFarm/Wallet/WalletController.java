package com.EquiFarm.EquiFarm.Wallet;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/wallet")
@Tag(name = "Wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/get")
    public ResponseEntity<?> getUserWallet() {
        return ResponseEntity.ok(walletService.getUserWallet());
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllWallet() {
        return ResponseEntity.ok(walletService.getAllWallets());
    }

    @GetMapping("/get/by/walletId/{walletId}")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getWalletByWalletId(@PathVariable("walletId") Long id) {
        return ResponseEntity.ok(walletService.getWalletById(id));
    }

    @DeleteMapping("/delete/{walletId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteWallet(@PathVariable("walletId") Long id) {
        return ResponseEntity.ok(walletService.deleteWallet(id));
    }

}
