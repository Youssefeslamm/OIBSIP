package com.youssefeslam.library;

import com.youssefeslam.library.entity.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DomainModelTest {

    private static final LocalDateTime ISSUE_TIME =
            LocalDateTime.of(2026, 8, 1, 10, 0);

    @Test
    void issuingAndReturningCopyUpdatesAvailableQuantity() {
        Book book = createBook(2);

        assertEquals(2, book.getAvailableQuantity());

        book.issueCopy();

        assertEquals(1, book.getAvailableQuantity());

        book.returnCopy();

        assertEquals(2, book.getAvailableQuantity());
    }

    @Test
    void issuingUnavailableBookIsRejected() {
        Book book = createBook(1);

        book.issueCopy();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                book::issueCopy
        );

        assertEquals(
                "No copies are currently available",
                exception.getMessage()
        );
    }

    @Test
    void archivedBookCannotBeIssued() {
        Book book = createBook(2);
        book.archive();

        assertFalse(book.isAvailable());

        assertThrows(
                IllegalStateException.class,
                book::issueCopy
        );
    }

    @Test
    void loanRoundsPartialOverdueDayUp() {
        Loan loan = new Loan(
                createUser(),
                createBook(1),
                ISSUE_TIME,
                14
        );

        LocalDateTime dueTime = ISSUE_TIME.plusDays(14);

        assertEquals(
                0,
                loan.calculateOverdueDays(dueTime)
        );

        assertEquals(
                1,
                loan.calculateOverdueDays(dueTime.plusMinutes(1))
        );

        assertEquals(
                2,
                loan.calculateOverdueDays(
                        dueTime.plusDays(1).plusMinutes(1)
                )
        );
    }

    @Test
    void returnedLoanCannotBeReturnedTwice() {
        Loan loan = new Loan(
                createUser(),
                createBook(1),
                ISSUE_TIME,
                14
        );

        loan.markReturned(ISSUE_TIME.plusDays(5));

        assertEquals(LoanStatus.RETURNED, loan.getStatus());

        assertThrows(
                IllegalStateException.class,
                () -> loan.markReturned(ISSUE_TIME.plusDays(6))
        );
    }

    @Test
    void fineUsesFiveCurrencyUnitsPerOverdueDay() {
        Loan loan = new Loan(
                createUser(),
                createBook(1),
                ISSUE_TIME,
                14
        );

        Fine fine = new Fine(loan, 3);

        assertEquals(
                new BigDecimal("15.00"),
                fine.getAmount()
        );
        assertEquals(FineStatus.UNPAID, fine.getStatus());

        fine.markPaid(ISSUE_TIME.plusDays(20));

        assertEquals(FineStatus.PAID, fine.getStatus());
        assertNotNull(fine.getPaidAt());
    }

    @Test
    void reservationFollowsExpectedStateTransitions() {
        Reservation reservation = new Reservation(
                createUser(),
                createBook(1)
        );

        assertEquals(
                ReservationStatus.WAITING,
                reservation.getStatus()
        );

        LocalDateTime availableTime =
                LocalDateTime.of(2026, 8, 10, 9, 0);

        reservation.makeAvailable(availableTime, 2);

        assertEquals(
                ReservationStatus.AVAILABLE,
                reservation.getStatus()
        );
        assertEquals(
                availableTime.plusDays(2),
                reservation.getAvailableUntil()
        );

        reservation.fulfill(availableTime.plusDays(1));

        assertEquals(
                ReservationStatus.FULFILLED,
                reservation.getStatus()
        );
    }

    @Test
    void reservationCannotBeFulfilledAfterCollectionWindow() {
        Reservation reservation = new Reservation(
                createUser(),
                createBook(1)
        );

        LocalDateTime availableTime =
                LocalDateTime.of(2026, 8, 10, 9, 0);

        reservation.makeAvailable(availableTime, 2);

        assertThrows(
                IllegalStateException.class,
                () -> reservation.fulfill(
                        availableTime.plusDays(2).plusMinutes(1)
                )
        );
    }

    private Book createBook(int quantity) {
        Category category = new Category(
                "Software Engineering",
                "Programming and software design"
        );

        return new Book(
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                category,
                quantity
        );
    }

    private User createUser() {
        return new User(
                "reader@example.com",
                "encoded-password",
                "Test Reader"
        );
    }
}