package com.example.project.service;

import com.example.project.entity.RefreshToken;
import com.example.project.entity.UserEntity;
import com.example.project.repository.RefreshTokenRepository;
import com.example.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService
{
    private final RefreshTokenRepository refreshTokenRepository;
    private  final UserRepository userRepository;

    public RefreshToken createRefreshToken(
            UserEntity user)
    {
        refreshTokenRepository
                .deleteByUser(user);

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setUser(user);

        refreshToken.setToken(
                UUID.randomUUID()
                        .toString()
        );

        refreshToken.setExpiryDate(
                LocalDateTime.now()
                        .plusDays(7)
        );

        return refreshTokenRepository
                .save(refreshToken);
    }



    public RefreshToken verifyRefreshToken(String token)
    {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now()))
        {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    @Transactional
    public void logout(String username)
    {
        UserEntity user = userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "User not found"
                                )
                        );

        refreshTokenRepository
                .deleteByUser(user);
    }
}