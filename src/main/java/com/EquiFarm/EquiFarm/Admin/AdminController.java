package com.EquiFarm.EquiFarm.Admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EquiFarm.EquiFarm.Admin.DTO.AdminUserRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAdminProfile() {
        return ResponseEntity.ok(adminService.getAdminProfile());
    }

    @GetMapping("/get/by/adminId/{adminId}")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAdminByAdminId(@PathVariable("adminId") Long id) {
        return ResponseEntity.ok(adminService.getAdminByAdminId(id));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> adminProfileUpdate(@RequestBody AdminUserRequest adminUserRequest) {
        return ResponseEntity.ok(adminService.adminProfileUpdate(adminUserRequest));
    }

    @PutMapping("/update/by/userId/{userId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> adminUpdate(@PathVariable("userId") Long userId,
                                         @RequestBody AdminUserRequest adminUserRequest) {
        return ResponseEntity.ok(adminService.updateAdminUserId(userId, adminUserRequest));
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> adminProfilePartialUpdate(@RequestBody AdminUserRequest adminUserRequest) {
        return ResponseEntity.ok(adminService.adminProfileUpdate(adminUserRequest));
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @GetMapping("/get/by/userId/{userId}")
    ResponseEntity<?> getByUserId(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(adminService.fetchAdminByUserId(userId));
    }

    @DeleteMapping("/delete/{adminId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteAdmin(@PathVariable("adminId") Long id) {
        return ResponseEntity.ok(adminService.adminDelete(id));
    }

}
