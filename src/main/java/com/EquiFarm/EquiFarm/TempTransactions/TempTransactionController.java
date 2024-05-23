package com.EquiFarm.EquiFarm.TempTransactions;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Temp Transactions")
@RequiredArgsConstructor
public class TempTransactionController {
    private final TempTransactionService transactionService;

    @PostMapping("enter/transaction/{orderId}")
//    @PreAuthorize("hasAuthority('customer:create')")
    public ResponseEntity<?> enterTransaction(@PathVariable("orderId") Long orderId){
        return ResponseEntity.ok(transactionService.enterTransaction(orderId));
    }
    @GetMapping("user/fetch/all/{userId}")
    public ResponseEntity<?> fetchBuyerUserIdOrSellerUserIdAll(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(transactionService.fetchBuyerUserIdOrSellerUserIdAllTransactions(userId));
    }
//    @GetMapping("seller/fetch/all/{userId}")
//    public ResponseEntity<?> fetchSellerAll(@PathVariable("userId") Long userId){
//        return ResponseEntity.ok(transactionService.fetchSellerAllTransactions(userId));
//    }
    @GetMapping("fetch/all")
    public ResponseEntity<?> fetchAll(){
        return ResponseEntity.ok(transactionService.fetchAllTransactions());
    }
}
