package com.Writam.ticketflow.ticket;


import com.Writam.ticketflow.ticket.dto.TicketResponse;
import com.Writam.ticketflow.ticket.dto.CreateTicketRequest;
import com.Writam.ticketflow.ticket.dto.UpdateAssignedIdRequest;
import com.Writam.ticketflow.ticket.dto.UpdateTicketStatusRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
/*POST endpoint /api/v1/tickets - any authenticated, creates ticket
GET /api/v1/tickets/{id} any authenticated, gets one ticket
GET /api/v1/tickets any authenticated List tickets( role based)
PATCH /api/v1/tickets/{id}/status AGENT or ADMIN Update status ( PreAuthorize)
To get current user's UUID, Jwt Authentication using @AuthenticationPrinciple string userId
then java.util.UUID.fromString(userId)
 */

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor

public class TicketController {


    private final TicketService ticketService;


    @PostMapping()
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request, @AuthenticationPrincipal String userId) {
        TicketResponse response = ticketService.create(request, UUID.fromString(userId) );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable UUID id ) {
        TicketResponse response = ticketService.getTicket(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping()
    public ResponseEntity<Page<TicketResponse>> listTickets(@AuthenticationPrincipal String userId, @RequestParam(required = false) TicketStatus status, Pageable pageable){
        String result = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().iterator().next().getAuthority();
        String role = result.substring(5);
        Page<TicketResponse> response = ticketService.listTickets(UUID.fromString(userId),role,status, pageable);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<TicketResponse> updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateTicketStatusRequest request) {
        TicketResponse response = ticketService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> assignTicket(@PathVariable UUID id, @Valid @RequestBody UpdateAssignedIdRequest request) {
        TicketResponse response = ticketService.assignTicket(id, request.agentId());
        return ResponseEntity.ok(response);
    }
}
