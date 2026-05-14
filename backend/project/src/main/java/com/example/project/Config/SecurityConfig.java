package com.example.project.Config;

import com.example.project.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity

public class SecurityConfig {

    @Autowired
    JwtFilter jwtFilter;


    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
    {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    )
            throws Exception
    {
        http.csrf(csrf -> csrf.disable())
                .logout(logout -> logout.disable())
                .headers(
                        headers ->
                                headers.frameOptions(
                                        frame ->
                                                frame.disable()
                                )
                )

                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )
                .cors(cors->{})

                .authorizeHttpRequests(
                        auth ->
                                auth

                                        .requestMatchers(
                                                "/api/auth/signup",
                                                "/api/auth/login",
                                                "/api/auth/refresh",
                                                "/h2-console/**"
                                        )
                                        .permitAll()

                                        .requestMatchers(
                                                "/api/admin/**"
                                        )
                                        .hasRole("ADMIN")

                                        .anyRequest()
                                        .authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration config =
                new CorsConfiguration();

        config.setAllowedOrigins(
                List.of(
                        "http://localhost:3000"
                )
        );

        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE"
                )
        );

        config.setAllowedHeaders(
                List.of("*")
        );

        config.setAllowCredentials(
                true
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;
    }

}


/*
AuthenticationManager
        ↓
CustomUserDetailsService
        ↓
UserRepository
        ↓
Database
        ↓
return Authentication obj

we dont manually write code all for this
-> here we have added that @Bean that return AuthenticationManager
-> in the login methode we just do
Authentication authentication=AuthenticationManager.authenticate( new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                  )

 -> it automatically checks the password ,if wrong the throws execption
-> till here token generation is done,we have not used this token ,end point is still open
 */