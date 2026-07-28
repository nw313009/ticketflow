package com.Writam.ticketflow.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        @NotBlank
        String content,

        boolean internal
) {
}
