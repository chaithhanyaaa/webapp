package com.example.project.DTO;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String email;
    private String otp;
    OtpVerificationRequest(String email,String otp)
    {
        this.email=email;
        this.otp=otp;
    }
}