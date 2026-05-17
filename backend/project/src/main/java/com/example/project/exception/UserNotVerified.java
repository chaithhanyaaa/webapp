package com.example.project.exception;

public class UserNotVerified extends  RuntimeException {
    public UserNotVerified(String message)
    {
        super(message);

    }
}
