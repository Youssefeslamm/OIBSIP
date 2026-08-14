package com.youssefeslam.library.dto.fine;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FineResponse(
        Long id,
        Long loanId,
        Long userId,
        String userEmail,
        String bookTitle,
        BigDecimal amount,
        int overdueDays,
        String status,
        LocalDateTime createdAt,
        LocalDateTime paidAt
) {
}