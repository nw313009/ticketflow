package com.Writam.ticketflow.ticket;


import com.Writam.ticketflow.ticket.dto.TicketResponse;
import com.Writam.ticketflow.ticket.dto.CreateTicketRequest;
import com.Writam.ticketflow.ticket.dto.UpdateTicketStatusRequest;
import java.util.Optional;
import java.util.UUID;


import com.Writam.ticketflow.user.Role;
import com.Writam.ticketflow.user.User;
import com.Writam.ticketflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    @Transactional
    public TicketResponse create(CreateTicketRequest request, UUID creatorId) {
        User user = userRepository.findById(creatorId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Ticket ticket = Ticket.builder()
                .title(request.title())
                .description(request.description())
                .priority(request.priority())
                .creator(user).build();


        Ticket saved = ticketRepository.save(ticket);
        Optional<User> leastLoadedAgent = userRepository.findLeastLoadedAgent();
        leastLoadedAgent.ifPresent(value -> {
            saved.setAssigned(value);
            ticketRepository.save(ticket);
        });

       return buildTicketResponse(saved);

    }


    public TicketResponse getTicket(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(()-> new IllegalArgumentException("Ticket not found"));
        return buildTicketResponse(ticket);
    }
    @Transactional
    public TicketResponse updateStatus(UUID ticketID, UpdateTicketStatusRequest request ) {
        Ticket ticket = ticketRepository.findById(ticketID).orElseThrow(()-> new IllegalArgumentException("Ticket not found") );
        if(ticket.getStatus().canTransitionTo(request.status())){
            ticket.setStatus(request.status());
            Ticket saved = ticketRepository.saveAndFlush(ticket);
            return buildTicketResponse(saved);
        }else {
            throw new IllegalArgumentException("Cannot transition from " + ticket.getStatus() + " to " + request.status());
        }



    }

    @Transactional
    public TicketResponse assignTicket(UUID ticketId, UUID assigneeId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(()-> new IllegalArgumentException("Ticket not found"));
        User assignee = userRepository.findById(assigneeId).orElseThrow(()-> new IllegalArgumentException("User not found"));
        if(assignee.getRole() != Role.AGENT){
            throw new IllegalArgumentException("Only agents can assign tickets");
        }
        ticket.setAssigned(assignee);
        Ticket saved = ticketRepository.saveAndFlush(ticket);
        return buildTicketResponse(saved);
    }

    public Page<TicketResponse> listTickets(UUID userId, String role, TicketStatus status, Pageable pageable) {
        Page<Ticket> ticketsPage;
        if (role.equals("CUSTOMER")) {
            User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("no user found"));
            if (status != null) {
                ticketsPage = ticketRepository.findByCreatorAndStatus(user, status,pageable);
            } else {
                ticketsPage = ticketRepository.findByCreator(user, pageable);
            }

        }else {
            //Assume non customers like admin or agent
            if(status!=null) {
                ticketsPage = ticketRepository.findByStatus(status,pageable);
            }else{
                ticketsPage = ticketRepository.findAll(pageable);
            }


        }
        //Map list to responses with stream
        return ticketsPage.map(this::buildTicketResponse);
    }


    private TicketResponse buildTicketResponse(Ticket ticket){


        //extract assigned userID
        UUID assignedToId = Optional.ofNullable(ticket.getAssigned()).map(User::getId).orElse(null);
        //extract assigned user name
        String assignedToName = Optional.ofNullable(ticket.getAssigned()).map(User::getFullName).orElse(null);

        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreator().getId(),
                ticket.getCreator().getFullName(),
                assignedToId,
                assignedToName,
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }


}
