package com.example.project.service;


import com.example.project.DTO.*;
import com.example.project.entity.OtpVerification;
import com.example.project.entity.RefreshToken;
import com.example.project.entity.UserEntity;
import com.example.project.exception.NoEmailFoundException;
import com.example.project.exception.UserAlreadyExistsException;
import com.example.project.exception.UserNotVerified;
import com.example.project.repository.OtpVerificationRepository;
import com.example.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private  final  OtpService otpService;
    private  final  EmailService emailService;
    private final OtpVerificationRepository otpVerificationRepository;

    public String signup(SignupRequest request)
    {
        /*
        Can username already exist?
        Can email already exist?
        */
        if(userRepository.existsByUsername(request.getUsername()))
        {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if(userRepository.existsByEmail(request.getEmail()))
        {
            throw new UserAlreadyExistsException("Email already exists");
        }
        if(request.getEmail()==null || request.getFirstName()==null || request.getLastName()==null)
        {
            throw new RuntimeException("fill all the fields");
        }



        String hashedPassword = passwordEncoder.encode(request.getPassword());

        String otp = otpService.generateOtp();
        UserEntity user=new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setEmail(request.getEmail());
        user.setLastName(request.getLastName());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        otpService.saveOtp(
                request.getEmail(),
                request.getUsername(),
                otp
        );



        return "OTP sent to email";
    }

    public LoginResponse login(LoginRequest request)
    {

        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        /*
        new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                        )
        This does NOT create JWT.
        This does NOT authenticate user.

        we send this to authenticationManager then it asks CustomUserDetailsService internally the code runs
        db checks , it checks password match or not

        -> we don't manually do if(user.getPassword().equals(...))
        -> if pass word is wrong it throws exception BadCredentialsException()


         */
        UserDetails userDetails = (UserDetails)authentication.getPrincipal();
        String jwttoken=jwtService.generateToken(userDetails.getUsername());
        System.out.println(userDetails.getUsername());


        UserEntity user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(jwttoken, refreshToken.getToken());

    }


    public LoginResponse refreshToken(RefreshRequest request)
    {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());

        UserEntity user = refreshToken.getUser();

        String accessToken =
                jwtService.generateToken(user.getUsername());

        return new LoginResponse(
                accessToken,
                refreshToken.getToken()
        );
    }


    public String logout()
    {
        System.out.println("logout reached");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        refreshTokenService.logout(username);
        return "Logged out successfully";
    }

    @Transactional
    public String verifyOtpForSignup(OtpVerificationRequest request)
    {

        OtpVerification record = otpService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );

        UserEntity user =userRepository.findByEmail(record.getEmail()) .orElseThrow(() -> new NoEmailFoundException("Email is wrong"));;

        user.setUsername(record.getUsername());
        user.setVerified(true);
        user.setRole("USER");

        userRepository.save(user);

        otpVerificationRepository.delete(record);

        emailService.sendWelcomeEmail(record.getEmail(), record.getUsername());

        return "Signup successful";
    }

    public String SendOtpForForgotPassword(ResendOtp request)
    {

        UserEntity record = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoEmailFoundException("Email not found"));
        if(record.isVerified()==false)
            throw new UserNotVerified("User Not Verified with OTP");

        String otp = otpService.generateOtp();
        otpService.saveOtp(request.getEmail(), record.getUsername(), otp);

        return "OTP sent";
    }


    public String resetPassword(ResetPasswordDto request)
    {
        String email=request.getEmail();
        String otp=request.getOtp();
        OtpVerification record = otpService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );
        UserEntity user =userRepository.findByEmail(record.getEmail()) .orElseThrow(() -> new NoEmailFoundException("Email is wrong"));
        if(!user.isVerified())
        throw  new UserNotVerified("Verfication has not done for this account ,Verify First to reset password");
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return "password is reset";

    }
}