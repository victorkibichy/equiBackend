package com.EquiFarm.EquiFarm.Insurance.TypeOfInsurance;

import com.EquiFarm.EquiFarm.Insurance.TypeOfInsurance.DTO.TypeOfInsuranceRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/type/of/insurance")
@Tag(name = "Type of Insurance", description = "This are the types of insurance available for the farmers.")
@RequiredArgsConstructor
public class TypeOfInsuranceController {
    private final TypeOfInsuranceService typeOfInsuranceService;

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllTypeOfInsurance() {
        return ResponseEntity.ok(typeOfInsuranceService.getAllTypeOfInsurance());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> createTypeOfInsurance(@RequestBody TypeOfInsuranceRequest typeOfInsuranceRequest) {
        return ResponseEntity.ok(typeOfInsuranceService.createTypeOfInsurance(typeOfInsuranceRequest));
    }

    @GetMapping("/get/by/typeOfInsuranceId/{typeOfInsuranceId}")
    public ResponseEntity<?> getTypeOfInsuranceByTypeOfInsuranceId(@PathVariable("typeOfInsuranceId") Long id) {
        return ResponseEntity.ok(typeOfInsuranceService.getTypeOfInsuranceById(id));
    }

    @PutMapping("/update/{typeOfInsuranceId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateTypeOfInsurance(@PathVariable("typeOfInsuranceId") Long id,
                                                  @RequestBody TypeOfInsuranceRequest typeOfInsuranceRequest) {
        return ResponseEntity.ok(typeOfInsuranceService.updateTypeOfInsurance(typeOfInsuranceRequest, id));
    }

    @DeleteMapping("/delete/{typeOfInsuranceId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteTypeOfInsurance(@PathVariable("typeOfInsuranceId") Long id) {
        return ResponseEntity.ok(typeOfInsuranceService.typeOfInsuranceDelete(id));
    }
}
