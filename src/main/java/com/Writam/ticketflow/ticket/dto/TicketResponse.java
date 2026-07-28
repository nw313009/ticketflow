package com.Writam.ticketflow.ticket.dto;

import com.Writam.ticketflow.ticket.Priority;
import com.Writam.ticketflow.ticket.TicketStatus;
import java.util.UUID;
import java.time.LocalDateTime;
public record TicketResponse(
      UUID id,
      String title,
      String description,
      TicketStatus status,
      Priority priority,
      UUID creatorId,
      String creatorName,
      UUID assignedToId,
      String assignedToName,
      LocalDateTime createdAt,
      LocalDateTime updatedAt

) {
}
