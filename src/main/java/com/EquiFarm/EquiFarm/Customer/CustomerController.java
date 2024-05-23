package com.EquiFarm.EquiFarm.Customer;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EquiFarm.EquiFarm.Customer.DTO.CustomerRequest;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/customer")
@Tag(name = "Customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('customer:read')")
    public ResponseEntity<?> getCustomerProfile() {
        return ResponseEntity.ok(customerService.getCustomerProfile());
    }

    @GetMapping("/get/by/customerId/{customerId}")
    public ResponseEntity<?> getCustomerById(@PathVariable("customerId") Long id) {
        return ResponseEntity.ok(customerService.getCustomerByProfileId(id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('customer:update')")
    public ResponseEntity<?> updateCustomerProfile(@RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.customerProfileUpdate(customerRequest));
    }
    
    @PutMapping("/update/{customerId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> customerProfileUpdate(@PathVariable("customerId") Long id, @RequestBody CustomerRequest customerRequest){
        return ResponseEntity.ok(customerService.updateCustomerProfile(customerRequest, id));
    }

    @DeleteMapping("/delete/{customerId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteCustomer(@PathVariable("customerId") Long id) {
        return ResponseEntity.ok(customerService.customerDelete(id));
    }

}
