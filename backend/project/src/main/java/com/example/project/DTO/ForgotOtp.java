package com.example.project.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;
@Data
public class ForgotOtp
{
    private String email;
    public ForgotOtp(String email)
    {
        this.email=email;
    }
}
