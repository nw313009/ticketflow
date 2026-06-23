package com.Writam.ticketflow.auth;

import com.Writam.ticketflow.user.Role;
import com.Writam.ticketflow.user.User;
import com.Writam.ticketflow.user.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
@Transactional

public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Value("${jwt.secret}")
    private String jwtSecret;


    @Test
    void login_withValidCredentials_returnsOkWithTokens() throws Exception {
        User user = User.builder()
                .email("test@test.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Test User")
                .role(Role.CUSTOMER)
                .build();
        userRepository.save(user);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }


    // TODO: uncomment and update after GlobalExceptionHandler (Week 3)
// Expected: wrong password → 401, nonexistent email → 401
// Currently throws unhandled IllegalArgumentException

    //@Test
    /*void login_withWrongPassword_returns401() throws Exception {
        User user = User.builder()
                .email("test@test.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Test User")
                .role(Role.CUSTOMER)
                .build();
        userRepository.save(user);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void login_withNonexistentEmail_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nonexistent@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isInternalServerError());
    } */

    @Test
    void authenticatedRequest_withExpiredToken_returns403() throws Exception {
        SecretKey signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        String expiredToken = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .claim("email", "test@test.com")
                .claim("role", "CUSTOMER")
                .issuedAt(new Date(System.currentTimeMillis() - 60000))
                .expiration(new Date(System.currentTimeMillis() - 30000))
                .signWith(signingKey)
                .compact();

        mockMvc.perform(get("/api/v1/tickets")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedRequest_withTamperedToken_returns403() throws Exception {
        String tamperedToken = "eyJhbGciOiJIUzM4NCJ9.tampered.invalid";

        mockMvc.perform(get("/api/v1/tickets")
                        .header("Authorization", "Bearer " + tamperedToken))
                .andExpect(status().isForbidden());
    }



}
