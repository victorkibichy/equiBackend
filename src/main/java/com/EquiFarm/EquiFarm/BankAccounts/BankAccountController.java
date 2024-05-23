package com.EquiFarm.EquiFarm.BankAccounts;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EquiFarm.EquiFarm.BankAccounts.DTO.BankAccountRequest;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

@RestController
@RequestMapping("/api/v1/bankAccounts")
@Tag(name = "Bank Accounts")
@RequiredArgsConstructor
public class BankAccountController {
    private final BankAccountService bankAccountService;

//    @PostMapping("/add")
//    public ResponseEntity<?> addBankAccount(@RequestBody BankAccountRequest bankAccountRequest) {
//        return ResponseEntity.ok(bankAccountService.addBankAccount(bankAccountRequest));
//    }

//    @PostMapping("/staff/add")
//    @PreAuthorize("hasAuthority('admin:create')")
//    public ResponseEntity<?> staffBankAccountCreation(@RequestBody BankAccountRequest bankAccountRequest) {
//        return ResponseEntity.ok(bankAccountService.staffBankAccountCreation(bankAccountRequest));
//    }

    @GetMapping("/get/all/user/bankAccounts")
    public ResponseEntity<?> userGetAllBankAccounts() { 
        return ResponseEntity.ok(bankAccountService.getBankAccountByUser());
    }


    @GetMapping("/staff/get/all/user/bankAccounts/by/userId/{userId}")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> staffGetAllUserBankAccountsByUserId(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(bankAccountService.getBankAccountByUserId(id));
    }

//    @PutMapping("/update/{bankAccountId}")
//    @PreAuthorize("hasAuthority('admin:update')")
//    public ResponseEntity<?> updateBankAccount(@RequestBody BankAccountRequest bankAccountRequest,
//            @PathVariable("bankAccountId") Long id) {
//        return ResponseEntity.ok(bankAccountService.updateBankAccount(bankAccountRequest, id));
//    }

    @GetMapping("/get/by/bankAccountId/{bankAccountId}")
    public ResponseEntity<?> getBankAccountByAccountId(@PathVariable("bankAccountId") Long id) {
        return ResponseEntity.ok(bankAccountService.getBankAccountById(id));
    }

    @DeleteMapping("/delete/{bankAccountId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteBankAccount(@PathVariable("bankAccountId") Long id) {
        return ResponseEntity.ok(bankAccountService.bankAccountDelete(id));
    }

}
