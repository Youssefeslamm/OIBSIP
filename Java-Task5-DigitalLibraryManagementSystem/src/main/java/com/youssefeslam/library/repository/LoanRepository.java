package com.youssefeslam.library.repository;

import com.youssefeslam.library.entity.Loan;
import com.youssefeslam.library.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserIdOrderByIssuedAtDesc(Long userId);

    List<Loan> findByStatusInOrderByDueAtAsc(
            Collection<LoanStatus> statuses
    );

    List<Loan> findByStatusInAndDueAtBefore(
            Collection<LoanStatus> statuses,
            LocalDateTime currentTime
    );

    boolean existsByUserIdAndBookIdAndStatusIn(
            Long userId,
            Long bookId,
            Collection<LoanStatus> statuses
    );

    Optional<Loan> findByIdAndUserId(Long loanId, Long userId);
}