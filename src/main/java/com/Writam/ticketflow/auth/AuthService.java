package com.Writam.ticketflow.auth;

import com.Writam.ticketflow.auth.dto.AuthResponse;
import com.Writam.ticketflow.auth.dto.RegisterRequest;
import com.Writam.ticketflow.user.Role;
import com.Writam.ticketflow.user.User;
import com.Writam.ticketflow.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service //business logic bean - manages lifecycle
@RequiredArgsConstructor //Lombok'sconstructor. generates with all final fields. Constructor injection, does not let final fields be null. app fails to start immediately
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //no beans of passwordEncoder found.

    @Transactional //wraps the method in db transaction. makes sure state commits fully. Habit to wrap methods that handle db writes and deletes.
    public AuthResponse register(RegisterRequest request) {
        //check if email already exists in user Repository which stores incoming requests' email
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered: " + request.email());

        }

        //Build user entity with hashed password
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))//one way hashing you cant reverse it.
                .fullName(request.fullName())
                .role(Role.CUSTOMER)//dont let users choose their own role.
                .build();

        // Save to database
        User saved = userRepository.save(user);

        // Return response DTO(never return the entity)

        return new AuthResponse(
           saved.getId(),
           saved.getEmail(),
           saved.getFullName(),
           saved.getRole().name()//converts enum to string
        );
    }
}
