package com.example.project.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class EmailService
{
    private final JavaMailSender mailSender;

    @Async
    public void sendWelcomeEmail(String toEmail, String username) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Welcome to ChatApp");
        message.setText(
                "Hi " + username + ",\n\n" +
                        "Thank you for registering on ChatApp.\n\n" +
                        "Happy chatting!"
        );

        mailSender.send(message);
    }

    @Async
    public void sendOtpEmail(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("ChatApp OTP Verification");

        message.setText(
                "Your OTP is: " + otp + "\n\n" +
                        "This OTP will expire in 5 minutes."
        );

        mailSender.send(message);
    }
}
