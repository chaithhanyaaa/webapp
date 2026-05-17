package com.example.project.exception;

import com.example.project.DTO.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(UserAlreadyExistsException ex)
    {
        ErrorResponse error = new ErrorResponse(
                        java.time.LocalDateTime
                                .now()
                                .toString(),

                        HttpStatus.CONFLICT.value(),

                        HttpStatus.CONFLICT
                                .getReasonPhrase(),

                        ex.getMessage()
                );

        return ResponseEntity
                .status(
                        HttpStatus.CONFLICT
                )
                .body(error);
    }

    @ExceptionHandler(NoEmailFoundException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(NoEmailFoundException ex)
    {
        ErrorResponse error = new ErrorResponse(
                java.time.LocalDateTime
                        .now()
                        .toString(),

                HttpStatus.NOT_FOUND.value(),

                HttpStatus.CONFLICT
                        .getReasonPhrase(),

                ex.getMessage()
        );

        return ResponseEntity
                .status(
                        HttpStatus.CONFLICT
                )
                .body(error);
    }

    @ExceptionHandler(UserNotVerified.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(UserNotVerified ex)
    {
        ErrorResponse error=new ErrorResponse(
                java.time.LocalDateTime.now().toString(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(
                        HttpStatus.FORBIDDEN
                )
                .body(error);

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex)
    {
        ErrorResponse error = new ErrorResponse(
                java.time.LocalDateTime
                        .now()
                        .toString(),

                HttpStatus.CONFLICT.value(),

                HttpStatus.CONFLICT
                        .getReasonPhrase(),

                ex.getMessage()
        );

        return ResponseEntity
                .status(
                        HttpStatus.CONFLICT
                )
                .body(error);
    }



    //authentication manager automatically throws this
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse>
    handleBadCredentials(
            BadCredentialsException ex
    )
    {
        ErrorResponse error =
                new ErrorResponse(
                        java.time.LocalDateTime
                                .now()
                                .toString(),

                        HttpStatus.UNAUTHORIZED.value(),

                        HttpStatus.UNAUTHORIZED
                                .getReasonPhrase(),

                        "Invalid username or password"
                );

        return ResponseEntity
                .status(
                        HttpStatus.UNAUTHORIZED
                )
                .body(error);
    }




    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>
    handleValidation(
            MethodArgumentNotValidException ex
    )
    {
        String message =
                ex.getBindingResult()
                        .getFieldError()
                        .getDefaultMessage();

        ErrorResponse error =
                new ErrorResponse(
                        java.time.LocalDateTime
                                .now()
                                .toString(),

                        HttpStatus.BAD_REQUEST.value(),

                        "Validation Error",

                        message
                );

        return ResponseEntity
                .badRequest()
                .body(error);
    }
}
