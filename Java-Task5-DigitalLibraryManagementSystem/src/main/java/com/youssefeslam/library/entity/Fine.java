package com.youssefeslam.library.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "fines")
public class Fine {

    private static final BigDecimal DAILY_RATE =
            new BigDecimal("5.00");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false, unique = true)
    private Loan loan;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "overdue_days", nullable = false)
    private int overdueDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FineStatus status;

    @Column(name = "created_at", nullable = false,
            insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    protected Fine() {
    }

    public Fine(Loan loan, int overdueDays) {
        if (overdueDays <= 0) {
            throw new IllegalArgumentException(
                    "A fine requires at least one overdue day"
            );
        }

        this.loan = Objects.requireNonNull(loan);
        this.overdueDays = overdueDays;
        this.amount = DAILY_RATE.multiply(
                BigDecimal.valueOf(overdueDays)
        );
        this.status = FineStatus.UNPAID;
    }

    public void markPaid(LocalDateTime paymentTime) {
        Objects.requireNonNull(paymentTime);

        if (status == FineStatus.PAID) {
            throw new IllegalStateException(
                    "This fine is already paid"
            );
        }

        status = FineStatus.PAID;
        paidAt = paymentTime;
    }

    public boolean isPaid() {
        return status == FineStatus.PAID;
    }

    public Long getId() {
        return id;
    }

    public Loan getLoan() {
        return loan;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public FineStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Fine fine)) {
            return false;
        }

        return id != null && Objects.equals(id, fine.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}