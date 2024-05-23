package com.EquiFarm.EquiFarm.AgriBusiness.TypeOfBusiness;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EquiFarm.EquiFarm.AgriBusiness.TypeOfBusiness.DTO.TypeOfBusinessRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/type/of/business")
@Tag(name = "Type of Business", description = "This are the type of business that are carried out by AgriBusinesses.")
@RequiredArgsConstructor
public class TypeOfBusinessController {
    private final TypeOfBusinessService typeOfBusinessService;

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllTypeOfBusiness() {
        return ResponseEntity.ok(typeOfBusinessService.getAllTypeOfBusiness());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> createTypeOfBusiness(@RequestBody TypeOfBusinessRequest typeOfBusinessRequest) {
        return ResponseEntity.ok(typeOfBusinessService.createTypeOfBusiness(typeOfBusinessRequest));
    }

    @GetMapping("/get/by/typeOfBusinessID/{typeOfBusinessID}")
    public ResponseEntity<?> getTypeOfBusinessByTypeOfBusinessId(@PathVariable("typeOfBusinessID") Long id) {
        return ResponseEntity.ok(typeOfBusinessService.getTypeOfBusinessById(id));
    }

    @PutMapping("/update/{typeOfBusinessID}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateTypeOfBusiness(@PathVariable("typeOfBusinessID") Long id,
            @RequestBody TypeOfBusinessRequest typeOfBusinessRequest) {
        return ResponseEntity.ok(typeOfBusinessService.updateTypeOfBusiness(typeOfBusinessRequest, id));
    }

    @DeleteMapping("/delete/{typeOfBusinessID}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteTypeOfBusiness(@PathVariable("typeOfBusinessID") Long id) {
        return ResponseEntity.ok(typeOfBusinessService.typeOfBusinessDelete(id));
    }

}
