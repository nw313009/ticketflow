package com.Writam.ticketflow.comment;

import com.Writam.ticketflow.comment.dto.CommentResponse;
import com.Writam.ticketflow.comment.dto.CreateCommentRequest;
import com.Writam.ticketflow.ticket.Ticket;
import com.Writam.ticketflow.ticket.TicketRepository;
import com.Writam.ticketflow.user.Role;
import com.Writam.ticketflow.user.User;
import com.Writam.ticketflow.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    @Transactional
    public CommentResponse addComment(CreateCommentRequest request, UUID ticketId, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        if(user.getRole() == Role.CUSTOMER && request.internal()){
            throw new IllegalArgumentException("Customers cannot add internal comments");
        }

        Comment comment = Comment.builder()
                .content(request.content())
                .ticket(ticket).internal(request.internal())
                .user(user).build();


        Comment saved = commentRepository.save(comment);
        return buildCommentResponse(saved);
    }

    public Page<CommentResponse> getComment(UUID ticketId, String role, Pageable pageable){
        Page<Comment> commentPage;
        if(role.equals("CUSTOMER")){
            commentPage = commentRepository.findByTicketIdAndInternalFalse(ticketId, pageable);
        }else {
            commentPage = commentRepository.findByTicketId(ticketId, pageable);
        }
        return commentPage.map(this::buildCommentResponse);

    }

    private CommentResponse buildCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getTicket().getId(),
                comment.getUser().getFullName(),
                comment.getTicket().getTitle(),
                comment.isInternal(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }




}

