package com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck;

import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.DTO.QualityCheckRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse/products/quality/checks")
@Tag(name = "Warehouse Quality Checks")
@Slf4j
@RequiredArgsConstructor
public class QualityCheckController {
    private final QualityCheckService qualityCheckService;

//    @PostMapping("/new")
//    @PreAuthorize("hasAuthority('admin:create')")
//    public ResponseEntity<?> addQualityCheck(@RequestBody QualityCheckRequest qualityCheckRequest){
//        return ResponseEntity.ok(qualityCheckService.addQualityCheck(qualityCheckRequest));
//    }

    @GetMapping("/all")
    public  ResponseEntity<?> getAllChecks(){
        return ResponseEntity.ok(qualityCheckService.getAll());
    }
    @PutMapping("/update/{checkId}")
    @PreAuthorize("hasAuthority('warehouse:update')")
    public ResponseEntity<?> updateQualityCheck(@PathVariable("checkId") Long checkId,
                                                @RequestBody QualityCheckRequest qualityCheckRequest){
        return ResponseEntity.ok(qualityCheckService.updateQualityCheck(checkId, qualityCheckRequest));
    }

    @DeleteMapping("/delete/{checkId}")
    @PreAuthorize("hasAuthority('warehouse:delete')")
    public ResponseEntity<?> deleteQualityCheck(@PathVariable("checkId") Long checkId){
        return ResponseEntity.ok(qualityCheckService.deletedQualityCheck(checkId));
    }
}
