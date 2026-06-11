package com.Writam.ticketflow.auth.dto;

import java.util.UUID;

public record AuthResponse(
        UUID id,
        String email,
        String fullName,
        String role
) {}

//notice no password hash because you dont want the user entity returned in its entirety, you would leak password hash to cliengt. Seperate response DTOs are needed