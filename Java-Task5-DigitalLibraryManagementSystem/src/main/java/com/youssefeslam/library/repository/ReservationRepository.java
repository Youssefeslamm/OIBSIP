package com.youssefeslam.library.repository;

import com.youssefeslam.library.entity.Reservation;
import com.youssefeslam.library.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserIdOrderByReservedAtDesc(
            Long userId
    );

    boolean existsByUserIdAndBookIdAndStatusIn(
            Long userId,
            Long bookId,
            Collection<ReservationStatus> statuses
    );

    Optional<Reservation> findByIdAndUserId(
            Long reservationId,
            Long userId
    );

    Optional<Reservation>
    findFirstByBookIdAndStatusOrderByReservedAtAsc(
            Long bookId,
            ReservationStatus status
    );
}