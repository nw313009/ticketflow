package com.Writam.ticketflow.ticket.dto;

import jakarta.validation.constraints.NotNull;
import com.Writam.ticketflow.ticket.TicketStatus;
public record UpdateTicketStatusRequest(
        @NotNull(message = "Status cannot be null")
        TicketStatus status

) {
}
