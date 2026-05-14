package com.example.project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse
{
    private String timestamp;

    private int status;

    private String error;

    private String message;
}