package com.Writam.ticketflow.ticket.dto;


import com.Writam.ticketflow.ticket.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketRequest (
    /*
    — what a customer sends to create a ticket
     (title, description, priority).
     Think about which fields the customer
      provides vs which the system sets.
     */
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "description is required")
    String description,

    @NotNull(message = "Priority is required")
    Priority priority){}
