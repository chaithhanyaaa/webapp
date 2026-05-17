package com.example.project.service;


import com.example.project.DTO.ForgotOtp;
import com.example.project.DTO.ResendOtp;
import com.example.project.entity.OtpVerification;
import com.example.project.repository.OtpVerificationRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@RequiredArgsConstructor
@Service
public class OtpService
{

    private final EmailService emailService;
    private  final OtpVerificationRepository otpRepository;

    public String generateOtp() {
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }
    public void sendOtpEmail(String email, String otp) {

        emailService.sendOtpEmail(email, otp);

    }

    @Transactional
    public void saveOtp(String email, String username, String otp) {

        Optional<OtpVerification> existing = otpRepository.findByEmail(email);

        existing.ifPresent(otpRepository::delete);

        OtpVerification record = new OtpVerification();

        record.setEmail(email);
        record.setUsername(username);
        record.setOtp(otp);
        record.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(record);
        emailService.sendOtpEmail(email,otp);
    }



    public OtpVerification verifyOtp(String email, String otp)
    {


        OtpVerification record = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if(record.getAttempts()>=5)
        {
            throw new RuntimeException("no of attempts are finished,click resend otp");
        }

        if (!record.getOtp().equals(otp)) {
            int attempts=record.getAttempts();
            attempts+=1;
            record.setAttempts(attempts);
            otpRepository.save(record);
            throw new RuntimeException("Invalid OTP");
        }

        if (record.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        return record;
    }




    public void resendOtp(ResendOtp request)
    {

        String email=request.getEmail();

        OtpVerification record = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No signup request found"));
        String newOtp = generateOtp();
        record.setOtp(newOtp);
        record.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        record.setAttempts(0);
        otpRepository.save(record);
        sendOtpEmail(email, newOtp);
    }



}
