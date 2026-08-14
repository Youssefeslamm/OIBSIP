package com.youssefeslam.library.service;

import com.youssefeslam.library.dto.reservation.ReservationResponse;
import com.youssefeslam.library.entity.Book;
import com.youssefeslam.library.entity.Reservation;
import com.youssefeslam.library.entity.ReservationStatus;
import com.youssefeslam.library.entity.User;
import com.youssefeslam.library.exception.BusinessRuleException;
import com.youssefeslam.library.exception.ResourceNotFoundException;
import com.youssefeslam.library.repository.BookRepository;
import com.youssefeslam.library.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private static final int COLLECTION_WINDOW_DAYS = 2;

    private static final Set<ReservationStatus> ACTIVE_STATUSES =
            Set.of(
                    ReservationStatus.WAITING,
                    ReservationStatus.AVAILABLE
            );

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final Clock clock;

    public ReservationService(
            ReservationRepository reservationRepository,
            BookRepository bookRepository,
            UserService userService
    ) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.clock = Clock.systemUTC();
    }

    @Transactional
    public ReservationResponse create(
            String email,
            Long bookId
    ) {
        User user = userService.requireByEmail(email);

        Book book = bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with ID: " + bookId
                ));

        if (book.isArchived()) {
            throw new BusinessRuleException(
                    "Archived books cannot be reserved"
            );
        }

        if (book.getAvailableQuantity() > 0) {
            throw new BusinessRuleException(
                    "This book is currently available and can be issued"
            );
        }

        if (reservationRepository
                .existsByUserIdAndBookIdAndStatusIn(
                        user.getId(),
                        bookId,
                        ACTIVE_STATUSES
                )) {
            throw new BusinessRuleException(
                    "You already have an active reservation for this book"
            );
        }

        Reservation reservation = new Reservation(user, book);

        return toResponse(
                reservationRepository.save(reservation)
        );
    }

    public List<ReservationResponse> findMine(String email) {
        User user = userService.requireByEmail(email);

        return reservationRepository
                .findByUserIdOrderByReservedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ReservationResponse cancel(
            String email,
            Long reservationId
    ) {
        User user = userService.requireByEmail(email);

        Reservation reservation = reservationRepository
                .findByIdAndUserId(
                        reservationId,
                        user.getId()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with ID: "
                                + reservationId
                ));

        boolean wasAvailable =
                reservation.getStatus()
                        == ReservationStatus.AVAILABLE;

        Long bookId = reservation.getBook().getId();

        reservation.cancel(LocalDateTime.now(clock));

        if (wasAvailable) {
            notifyNextWaitingUser(bookId);
        }

        return toResponse(reservation);
    }

    @Transactional
    public void notifyNextWaitingUser(Long bookId) {
        reservationRepository
                .findFirstByBookIdAndStatusOrderByReservedAtAsc(
                        bookId,
                        ReservationStatus.WAITING
                )
                .ifPresent(reservation ->
                        reservation.makeAvailable(
                                LocalDateTime.now(clock),
                                COLLECTION_WINDOW_DAYS
                        )
                );
    }

    @Transactional
    public void validateAndFulfillForIssue(
            User user,
            Book book
    ) {
        Optional<Reservation> availableReservation =
                reservationRepository
                        .findFirstByBookIdAndStatusOrderByReservedAtAsc(
                                book.getId(),
                                ReservationStatus.AVAILABLE
                        );

        if (availableReservation.isEmpty()) {
            return;
        }

        Reservation reservation = availableReservation.get();

        if (!reservation.getUser()
                .getId()
                .equals(user.getId())) {
            throw new BusinessRuleException(
                    "This available copy is reserved for another user"
            );
        }

        reservation.fulfill(LocalDateTime.now(clock));
    }

    private ReservationResponse toResponse(
            Reservation reservation
    ) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getUser().getEmail(),
                reservation.getBook().getId(),
                reservation.getBook().getTitle(),
                reservation.getStatus().name(),
                reservation.getReservedAt(),
                reservation.getAvailableUntil(),
                reservation.getFulfilledAt(),
                reservation.getCancelledAt()
        );
    }
}