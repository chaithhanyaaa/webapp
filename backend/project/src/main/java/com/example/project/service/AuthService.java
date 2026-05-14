package com.example.project.service;


import com.example.project.DTO.LoginResponse;
import com.example.project.DTO.RefreshRequest;
import com.example.project.DTO.SignupRequest;
import com.example.project.entity.RefreshToken;
import com.example.project.entity.UserEntity;
import com.example.project.exception.UserAlreadyExistsException;
import com.example.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.project.DTO.LoginRequest;


@Service
@RequiredArgsConstructor
public class AuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

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
        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setVerified(false);
        userRepository.save(user);

        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest request)
    {
        System.out.println(request.getUsername()+" "+request.getPassword());

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

}