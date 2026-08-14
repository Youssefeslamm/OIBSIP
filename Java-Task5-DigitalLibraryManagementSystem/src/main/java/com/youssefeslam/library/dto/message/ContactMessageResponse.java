package com.youssefeslam.library.dto.message;

import java.time.LocalDateTime;

public record ContactMessageResponse(
        Long id,
        Long userId,
        String userEmail,
        String userDisplayName,
        String subject,
        String message,
        String status,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt
) {
}