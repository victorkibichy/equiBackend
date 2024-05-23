package com.EquiFarm.EquiFarm.auth;

import com.EquiFarm.EquiFarm.auth.DTO.AuthenticationRequest;
import com.EquiFarm.EquiFarm.auth.DTO.RegisterRequest;
import com.EquiFarm.EquiFarm.auth.DTO.SignoutRequest;
import io.jsonwebtoken.io.IOException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<?> register(
      @RequestBody @Validated RegisterRequest request) {
    return ResponseEntity.ok(service.register(request));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<?> authenticate(
      @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> signout(@RequestBody SignoutRequest signoutRequest) {
    return ResponseEntity.ok(service.signOut(signoutRequest));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    service.refreshToken(request, response);
  }

}
