package com.Writam.ticketflow.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByTicketId(UUID ticketId, Pageable pageable);

    Page<Comment> findByTicketIdAndInternalFalse(UUID ticketId, Pageable pageable);
}
