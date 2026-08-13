package com.youssefeslam.library.repository;

import com.youssefeslam.library.entity.Fine;
import com.youssefeslam.library.entity.FineStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Long> {

    Optional<Fine> findByLoanId(Long loanId);

    List<Fine> findByLoanUserIdOrderByCreatedAtDesc(Long userId);

    List<Fine> findByStatusOrderByCreatedAtDesc(FineStatus status);
}