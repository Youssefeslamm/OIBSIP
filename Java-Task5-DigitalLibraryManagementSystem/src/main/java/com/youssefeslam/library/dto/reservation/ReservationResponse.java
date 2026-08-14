package com.youssefeslam.library.dto.reservation;

import java.time.LocalDateTime;


public record ReservationResponse(
        Long id,
        Long userId,
        String userEmail,
        Long bookId,
        String bookTitle,
        String status,
        LocalDateTime reservedAt,
        LocalDateTime availableUntil,
        LocalDateTime fulfilledAt,
        LocalDateTime cancelledAt
) {
}