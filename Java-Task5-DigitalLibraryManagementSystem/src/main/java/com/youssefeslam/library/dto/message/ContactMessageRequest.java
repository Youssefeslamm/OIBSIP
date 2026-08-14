package com.youssefeslam.library.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactMessageRequest(

        @NotBlank(message = "Subject is required")
        @Size(
                max = 150,
                message = "Subject cannot exceed 150 characters"
        )
        String subject,

        @NotBlank(message = "Message is required")
        @Size(
                max = 5000,
                message = "Message cannot exceed 5000 characters"
        )
        String message
) {
}