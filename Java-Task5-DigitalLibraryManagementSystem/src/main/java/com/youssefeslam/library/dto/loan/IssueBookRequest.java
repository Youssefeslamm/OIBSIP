package com.youssefeslam.library.dto.loan;

import jakarta.validation.constraints.NotNull;

public record IssueBookRequest(

        @NotNull(message = "Book ID is required")
        Long bookId
) {
}