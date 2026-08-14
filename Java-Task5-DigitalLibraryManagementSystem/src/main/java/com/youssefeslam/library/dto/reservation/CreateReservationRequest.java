package com.youssefeslam.library.dto.reservation;

import jakarta.validation.constraints.NotNull;

public record CreateReservationRequest(

        @NotNull(message = "Book ID is required")
        Long bookId
) {
}