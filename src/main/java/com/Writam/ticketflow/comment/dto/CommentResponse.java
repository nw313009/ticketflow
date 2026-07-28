package com.Writam.ticketflow.comment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        String content,
        UUID userId,
        UUID ticketId,
        String userName,
        String ticketTitle,
        boolean internal,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
