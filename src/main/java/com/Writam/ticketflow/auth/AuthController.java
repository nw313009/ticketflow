package com.Writam.ticketflow.auth;


import com.Writam.ticketflow.auth.dto.AuthResponse;
import com.Writam.ticketflow.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController //combines controller and responsebody. Every methods return value is serialized to JSON
@RequestMapping("/api/v1/auth")//base path for all endpoints in controller. v1 is api versioning, v2 will create without breaking existing clients.
@RequiredArgsConstructor //constructor with all final fields used for construction injection. In this case AuthService
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")//http post requests to this method
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {//Trigger for validation annotations on registerrequest
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
