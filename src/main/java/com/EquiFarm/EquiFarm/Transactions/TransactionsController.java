package com.EquiFarm.EquiFarm.Transactions;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions")
@RequiredArgsConstructor
public class TransactionsController {
    private final TransactionService transactionService;

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

}
