package com.EquiFarm.EquiFarm.EscrowWallet;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/escrowWallet")
@Tag(name = "Escrow Wallets")
@RequiredArgsConstructor
public class EscrowWalletController {
    private final EscrowWalletService escrowWalletService;

    @GetMapping("/get/all")
    // @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllEscrowWallet() {
        return ResponseEntity.ok(escrowWalletService.getAllEscrowWallets());
    }

    @GetMapping("/get/by/escrowWalletId/{escrowWalletId}")
    public ResponseEntity<?> getEscrowWalletById(@PathVariable("escrowWalletId") Long id) {
        return ResponseEntity.ok(escrowWalletService.getEscrowWalletById(id));
    }

}
