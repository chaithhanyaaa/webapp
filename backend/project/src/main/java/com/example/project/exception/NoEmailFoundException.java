package com.example.project.exception;

public class NoEmailFoundException extends  RuntimeException
{
    public NoEmailFoundException(String message)
    {
        super(message);

    }
}
