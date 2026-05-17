package com.example.project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class ResetPasswordDto {
    private String email;
    private String otp;
    private  String password;
}
