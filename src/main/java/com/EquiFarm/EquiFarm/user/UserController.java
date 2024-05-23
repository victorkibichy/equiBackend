package com.EquiFarm.EquiFarm.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {
    private final UserService userService;
    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> fetchAll(){
        return ResponseEntity.ok(userService.fetchAllActors());
    }

    @GetMapping("/get/by/id{userId}")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?> fetchActorById(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(userService.fetchActorById(userId));
    }
}
