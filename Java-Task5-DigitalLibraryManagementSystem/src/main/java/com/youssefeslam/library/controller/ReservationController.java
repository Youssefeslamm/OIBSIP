package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.reservation.CreateReservationRequest;
import com.youssefeslam.library.dto.reservation.ReservationResponse;
import com.youssefeslam.library.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(
            Authentication authentication,
            @Valid @RequestBody CreateReservationRequest request
    ) {
        return reservationService.create(
                authentication.getName(),
                request.bookId()
        );
    }

    @GetMapping("/me")
    public List<ReservationResponse> findMine(
            Authentication authentication
    ) {
        return reservationService.findMine(
                authentication.getName()
        );
    }

    @PatchMapping("/{reservationId}/cancel")
    public ReservationResponse cancel(
            Authentication authentication,
            @PathVariable Long reservationId
    ) {
        return reservationService.cancel(
                authentication.getName(),
                reservationId
        );
    }
}