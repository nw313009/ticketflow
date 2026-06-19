package com.Writam.ticketflow.auth;

import com.Writam.ticketflow.auth.dto.AuthResponse;
import com.Writam.ticketflow.auth.dto.LoginRequest;
import com.Writam.ticketflow.auth.dto.RegisterRequest;
import com.Writam.ticketflow.auth.dto.RefreshRequest;
import com.Writam.ticketflow.user.Role;
import com.Writam.ticketflow.user.User;
import com.Writam.ticketflow.user.UserRepository;

import java.util.UUID;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service //business logic bean - manages lifecycle
@RequiredArgsConstructor //Lombok'sconstructor. generates with all final fields. Constructor injection, does not let final fields be null. app fails to start immediately
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //no beans of passwordEncoder found.
    private final JwtService jwtService; //injected via RequiredArgsConstructor
    @Transactional //wraps the method in db transaction. makes sure state commits fully. Habit to wrap methods that handle db writes and deletes.
    public AuthResponse register(RegisterRequest request) {
        //check if email already exists in user Repository which stores incoming requests' email
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered: " + request.email());}

        //Build user entity with hashed password
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))//one way hashing you cant reverse it.
                .fullName(request.fullName())
                .role(Role.CUSTOMER)//dont let users choose their own role.
                .build();

        // Save to database
        User saved = userRepository.save(user);

        return buildAuthResponse(saved);
    }
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(()-> new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return buildAuthResponse(user);
         //Return response DTO(never return the entity)
    }
    public AuthResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();
        if (!jwtService.isTokenValid(token)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        Claims claims = jwtService.extractClaims(token);
        UUID userId = UUID.fromString(claims.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return buildAuthResponse(user);
    }
    private AuthResponse buildAuthResponse(User user) {//DRY extraction both register and login need to generate tokens and build same response, Private helper same pattern as buildToken
        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name()
        );
        String refreshToken = jwtService.generateRefreshToken(
                user.getId(), user.getEmail(), user.getRole().name()
        );


        return new AuthResponse(
           user.getId(),
           user.getEmail(),
           user.getFullName(),
           user.getRole().name(),//converts enum to string
           accessToken,
           refreshToken
        );
    }
}
