package com.EquiFarm.EquiFarm.Branch;

import com.EquiFarm.EquiFarm.Branch.DTO.BranchRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/branches")
@Tag(name = "Branches")
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> addBranch(@RequestBody BranchRequest branchRequest) {
        return ResponseEntity.ok(branchService.addBranch(branchRequest));
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllBranches() {
        return ResponseEntity.ok(branchService.fetchAllBranches());
    }

    @GetMapping("/get/by/branchId/{id}")
    public ResponseEntity<?> getBranchById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateBranch(@RequestBody BranchRequest branchRequest, @PathVariable("id") Long id) {
        return ResponseEntity.ok(branchService.updateBranch(branchRequest, id));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteBranch(@PathVariable("id") Long id) {
        return ResponseEntity.ok(branchService.removeBranch(id));
    }
}
