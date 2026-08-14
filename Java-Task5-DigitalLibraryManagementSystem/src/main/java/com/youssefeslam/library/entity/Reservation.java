package com.youssefeslam.library.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReservationStatus status;

    @Column(name = "reserved_at", nullable = false,
            insertable = false, updatable = false)
    private LocalDateTime reservedAt;

    @Column(name = "available_until")
    private LocalDateTime availableUntil;

    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    protected Reservation() {
    }

    public Reservation(User user, Book book) {
        this.user = Objects.requireNonNull(user);
        this.book = Objects.requireNonNull(book);
        this.status = ReservationStatus.WAITING;
    }

    public void makeAvailable(
            LocalDateTime currentTime,
            int collectionWindowDays
    ) {
        Objects.requireNonNull(
                currentTime,
                "Current time is required"
        );

        if (collectionWindowDays <= 0) {
            throw new IllegalArgumentException(
                    "Collection window must be greater than zero"
            );
        }

        if (status != ReservationStatus.WAITING) {
            throw new IllegalStateException(
                    "Only waiting reservations can become available"
            );
        }

        status = ReservationStatus.AVAILABLE;
        availableUntil = currentTime.plusDays(
                collectionWindowDays
        );
    }

    public void fulfill(LocalDateTime currentTime) {
        Objects.requireNonNull(currentTime);

        if (status != ReservationStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Only available reservations can be fulfilled"
            );
        }

        if (availableUntil != null
                && currentTime.isAfter(availableUntil)) {
            throw new IllegalStateException(
                    "The collection window has expired"
            );
        }

        status = ReservationStatus.FULFILLED;
        fulfilledAt = currentTime;
    }

    public void cancel(LocalDateTime currentTime) {
        Objects.requireNonNull(currentTime);

        if (status != ReservationStatus.WAITING
                && status != ReservationStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "This reservation can no longer be cancelled"
            );
        }

        status = ReservationStatus.CANCELLED;
        cancelledAt = currentTime;
    }

    public void expire(LocalDateTime currentTime) {
        Objects.requireNonNull(currentTime);

        if (status != ReservationStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Only available reservations can expire"
            );
        }

        if (availableUntil == null
                || !currentTime.isAfter(availableUntil)) {
            throw new IllegalStateException(
                    "The collection window has not expired"
            );
        }

        status = ReservationStatus.EXPIRED;
    }

    public boolean isActive() {
        return status == ReservationStatus.WAITING
                || status == ReservationStatus.AVAILABLE;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public LocalDateTime getAvailableUntil() {
        return availableUntil;
    }

    public LocalDateTime getFulfilledAt() {
        return fulfilledAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Reservation reservation)) {
            return false;
        }

        return id != null && Objects.equals(id, reservation.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}