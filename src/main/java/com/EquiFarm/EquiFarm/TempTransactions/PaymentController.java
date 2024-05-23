package com.EquiFarm.EquiFarm.TempTransactions;

import com.EquiFarm.EquiFarm.TempTransactions.DTO.FinRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment")
@RequiredArgsConstructor
public class PaymentController {
    private final FinService finService;
    @PostMapping("/add")
    public ResponseEntity<?> makeRequest(FinRequest finRequest) throws Exception {
        return ResponseEntity.ok(finService.fundTransfer(finRequest));
    }
}
