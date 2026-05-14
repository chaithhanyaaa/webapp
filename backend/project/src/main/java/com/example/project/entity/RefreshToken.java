package com.example.project.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class RefreshToken
{
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            unique = true,
            nullable = false
    )
    private String token;

    private LocalDateTime expiryDate;

    @OneToOne
    @JoinColumn(
            name = "user_id"
    )
    private UserEntity user;
}