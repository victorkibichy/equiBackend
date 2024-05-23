package com.EquiFarm.EquiFarm.AccountOpening;

import com.EquiFarm.EquiFarm.AccountOpening.DTO.CIFRequest;
import com.EquiFarm.EquiFarm.TempTransactions.FinService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/CIF")
@Tag(name = "CIF")
@RequiredArgsConstructor
public class CIFController {
    private final CIFService cifService;
    private final FinService finService;
//    @PostMapping("/add")
//    public ResponseEntity<?> addData(@RequestBody CIFRequest cifRequest){
//        return ResponseEntity.ok(cifService.addCIFData(cifRequest));
//    }

    @GetMapping("/fetch/{nationalId}/{firstName}")
    public ResponseEntity<?> fetchData(@PathVariable String nationalId,
                                       @PathVariable String firstName){
        return ResponseEntity.ok(cifService.fetchCIF(nationalId,firstName));
    }
    @GetMapping("/fetch/{nationalId}/")
    public ResponseEntity<?> fetchCifData(@PathVariable String nationalId) throws Exception {
        return ResponseEntity.ok(finService.fetchCifByIdNo(nationalId));
    }
    @GetMapping("/fetch/all")
    public ResponseEntity<?> fetchAll(){
        return ResponseEntity.ok(cifService.fetchAll());
    }
}
