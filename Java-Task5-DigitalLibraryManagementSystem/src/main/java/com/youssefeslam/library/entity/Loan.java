package com.youssefeslam.library.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "due_at", nullable = false)
    private LocalDateTime dueAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LoanStatus status;

    protected Loan() {
    }

    public Loan(User user, Book book, LocalDateTime issuedAt, int loanDays) {
        if (loanDays <= 0) {
            throw new IllegalArgumentException(
                    "Loan duration must be greater than zero"
            );
        }

        this.user = Objects.requireNonNull(user);
        this.book = Objects.requireNonNull(book);
        this.issuedAt = Objects.requireNonNull(issuedAt);
        this.dueAt = issuedAt.plusDays(loanDays);
        this.status = LoanStatus.ACTIVE;
    }

    public void markReturned(LocalDateTime returnTime) {
        Objects.requireNonNull(returnTime);

        if (status == LoanStatus.RETURNED) {
            throw new IllegalStateException(
                    "This loan has already been returned"
            );
        }

        if (returnTime.isBefore(issuedAt)) {
            throw new IllegalArgumentException(
                    "Return time cannot precede issue time"
            );
        }

        returnedAt = returnTime;
        status = LoanStatus.RETURNED;
    }

    public void updateOverdueStatus(LocalDateTime currentTime) {
        Objects.requireNonNull(currentTime);

        if (status == LoanStatus.ACTIVE && currentTime.isAfter(dueAt)) {
            status = LoanStatus.OVERDUE;
        }
    }

    public long calculateOverdueDays(LocalDateTime effectiveTime) {
        Objects.requireNonNull(effectiveTime);

        LocalDateTime comparisonTime =
                returnedAt != null ? returnedAt : effectiveTime;

        if (!comparisonTime.isAfter(dueAt)) {
            return 0;
        }

        long completeDays = ChronoUnit.DAYS.between(
                dueAt,
                comparisonTime
        );

        boolean hasPartialDay = comparisonTime.isAfter(
                dueAt.plusDays(completeDays)
        );

        return completeDays + (hasPartialDay ? 1 : 0);
    }

    public boolean isOpen() {
        return status == LoanStatus.ACTIVE
                || status == LoanStatus.OVERDUE;
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

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public LoanStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Loan loan)) {
            return false;
        }

        return id != null && Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}