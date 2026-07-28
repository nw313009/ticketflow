package com.Writam.ticketflow.ticket;

/*
Create ticket — customer submits a ticket (just save(), repository gets that for free)
Get ticket by ID — anyone authorized can view a single ticket
List tickets — customers see only their own, agents/admins see all, filtered by status
Update status — agent moves a ticket through the state machine
 */

import com.Writam.ticketflow.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID > {
    Page<Ticket> findByCreator(User creator, Pageable pageable);
    Page<Ticket> findByStatus(TicketStatus status,Pageable pageable);
    Page<Ticket> findByCreatorAndStatus(User creator,TicketStatus status,Pageable pageable);
}