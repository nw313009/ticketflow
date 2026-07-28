package com.Writam.ticketflow.ticket.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateAssignedIdRequest(
        @NotNull(message = "agentId cannot be null")
        UUID agentId
) {
}
