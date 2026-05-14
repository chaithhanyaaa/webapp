package com.example.project.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.security.Key;
import java.util.Date;

@Service
public class JwtService
{
    @Value("${jwt.secret}")
    private String secret;

    // we have secret key string,but inorder to work we have to have secretkey object ,this methode generates that
    private Key getSignKey()
    {
        return Keys.hmacShaKeyFor(
                secret.getBytes()
        );
    }

    public String generateToken(String username)
    {
        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(
                        new Date(System.currentTimeMillis())
                )
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000 * 60 * 15
                        )
                )
                .signWith(
                        getSignKey(),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }

    // Extract all claims
    private Claims extractAllClaims(String token)
    {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token)
    {
        return extractAllClaims(token).getExpiration().before(new Date());
    }


    public boolean isTokenValid(String token,String username)
    {
        final String extractedUsername = extractUsername(token);

        return extractedUsername.equals(username)
                &&
                !isTokenExpired(token);
    }


}
