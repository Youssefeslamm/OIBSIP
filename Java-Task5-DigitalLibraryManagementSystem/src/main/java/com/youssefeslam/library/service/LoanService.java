package com.youssefeslam.library.service;

import com.youssefeslam.library.dto.loan.LoanResponse;
import com.youssefeslam.library.entity.*;
import com.youssefeslam.library.exception.BusinessRuleException;
import com.youssefeslam.library.exception.ResourceNotFoundException;
import com.youssefeslam.library.repository.BookRepository;
import com.youssefeslam.library.repository.FineRepository;
import com.youssefeslam.library.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class LoanService {

    private static final int DEFAULT_LOAN_DAYS = 14;

    private static final Set<LoanStatus> OPEN_STATUSES =
            Set.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE);

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final FineRepository fineRepository;

    private final UserService userService;
    private final ReservationService reservationService;

    private final Clock clock;


    public LoanService(
            LoanRepository loanRepository,
            BookRepository bookRepository,
            FineRepository fineRepository,
            UserService userService,
            ReservationService reservationService
    ) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.fineRepository = fineRepository;
        this.userService = userService;
        this.reservationService = reservationService;
        this.clock = Clock.systemUTC();
    }

    @Transactional
    public LoanResponse issueBook(String email, Long bookId) {
        User user = userService.requireByEmail(email);

        Book book = bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with ID: " + bookId
                ));

        if (!user.isEnabled()) {
            throw new BusinessRuleException(
                    "Disabled accounts cannot issue books"
            );
        }

        if (loanRepository.existsByUserIdAndBookIdAndStatusIn(
                user.getId(),
                bookId,
                OPEN_STATUSES
        )) {
            throw new BusinessRuleException(
                    "You already have an active loan for this book"
            );
        }

        if (!book.isAvailable()) {
            throw new BusinessRuleException(
                    "No copies are currently available"
            );
        }

        book.issueCopy();

        Loan loan = new Loan(
                user,
                book,
                LocalDateTime.now(clock),
                DEFAULT_LOAN_DAYS
        );

        return toResponse(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse returnBook(String email, Long loanId) {
        User user = userService.requireByEmail(email);

        Loan loan = loanRepository.findByIdAndUserId(
                        loanId,
                        user.getId()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loan not found with ID: " + loanId
                ));

        if (!loan.isOpen()) {
            throw new BusinessRuleException(
                    "This loan has already been returned"
            );
        }

        Book book = bookRepository.findByIdForUpdate(
                        loan.getBook().getId()
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Associated book could not be found"
                ));

        LocalDateTime returnTime = LocalDateTime.now(clock);
        long overdueDays = loan.calculateOverdueDays(returnTime);

        loan.markReturned(returnTime);
        book.returnCopy();
        reservationService.notifyNextWaitingUser(book.getId());

        if (overdueDays > 0
                && fineRepository.findByLoanId(loanId).isEmpty()) {
            fineRepository.save(
                    new Fine(loan, Math.toIntExact(overdueDays))
            );
        }

        return toResponse(loan);
    }

    public List<LoanResponse> findMyLoans(String email) {
        User user = userService.requireByEmail(email);

        return loanRepository
                .findByUserIdOrderByIssuedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LoanResponse> findOpenLoans() {
        return loanRepository
                .findByStatusInOrderByDueAtAsc(OPEN_STATUSES)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private LoanResponse toResponse(Loan loan) {
        LocalDateTime now = LocalDateTime.now(clock);

        long overdueDays = loan.calculateOverdueDays(now);

        BigDecimal fineAmount = fineRepository
                .findByLoanId(loan.getId())
                .map(Fine::getAmount)
                .orElse(BigDecimal.ZERO);

        return new LoanResponse(
                loan.getId(),
                loan.getUser().getId(),
                loan.getUser().getEmail(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getIssuedAt(),
                loan.getDueAt(),
                loan.getReturnedAt(),
                loan.getStatus().name(),
                overdueDays,
                fineAmount
        );
    }
}