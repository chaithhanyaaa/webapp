package com.example.project.controller;

import com.example.project.DTO.*;
import com.example.project.service.AuthService;
import com.example.project.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class Auth
{
    private final AuthService authService;
    private  final OtpService otpService;

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

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody OtpVerificationRequest request)
    {
        return authService.verifyOtpForSignup(request);
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestBody ResendOtp request) {
        System.out.println(request.getEmail());
        otpService.resendOtp(request);
        return "OTP resent successfully";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ResendOtp request)
    {
        System.out.println(request);
        System.out.println(request.getEmail());

        return ResponseEntity.ok(
                authService.SendOtpForForgotPassword(request)
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto request)
    {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}