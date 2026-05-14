package com.example.project.controller;

import com.example.project.DTO.LoginRequest;
import com.example.project.DTO.LoginResponse;
import com.example.project.DTO.RefreshRequest;
import com.example.project.DTO.SignupRequest;
import com.example.project.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class Auth
{
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request
    )
    {
        return ResponseEntity.ok(
                authService.signup(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request)
    {

        return ResponseEntity.ok(
                authService.login(request));
    }


    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest request)
    {
        return ResponseEntity.ok(authService.refreshToken(request));
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout()
    {
        return ResponseEntity.ok(
                authService.logout()
        );
    }
}