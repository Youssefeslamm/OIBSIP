package com.youssefeslam.library.dto.loan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanResponse(
        Long id,
        Long userId,
        String userEmail,
        Long bookId,
        String bookTitle,
        LocalDateTime issuedAt,
        LocalDateTime dueAt,
        LocalDateTime returnedAt,
        String status,
        long overdueDays,
        BigDecimal fineAmount
) {
}