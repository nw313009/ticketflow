package com.Writam.ticketflow.auth;

import com.Writam.ticketflow.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtService(JwtProperties jwtProperties) {//hmacSha256 secret key object for signing in. creating cryptographic key is expensive reusing single instance is faster and cleaner
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
        this.accessTokenExpiration = jwtProperties.accessTokenExpiration();
        this.refreshTokenExpiration = jwtProperties.refreshTokenExpiration();
    }

    public String generateAccessToken(UUID userID, String email, String role) {
        return buildToken(userID,email,role,accessTokenExpiration);
    }

    public String generateRefreshToken(UUID userID, String email, String role) {
        return buildToken(userID,email,role, refreshTokenExpiration);

    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        }catch (JwtException e) {
            return false;
        }
    }

    private String buildToken(UUID userID, String email, String role, long expiration) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(userID.toString())
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(signingKey)
                .compact();
    }
}
