package com.Writam.ticketflow.auth.dto;


import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "refresh token not there")
        String refreshToken){}
