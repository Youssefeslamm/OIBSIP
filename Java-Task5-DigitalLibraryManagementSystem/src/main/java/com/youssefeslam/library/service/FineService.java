package com.youssefeslam.library.service;

import com.youssefeslam.library.dto.fine.FineResponse;
import com.youssefeslam.library.entity.Fine;
import com.youssefeslam.library.entity.FineStatus;
import com.youssefeslam.library.entity.User;
import com.youssefeslam.library.exception.BusinessRuleException;
import com.youssefeslam.library.exception.ResourceNotFoundException;
import com.youssefeslam.library.repository.FineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FineService {

    private final FineRepository fineRepository;
    private final UserService userService;
    private final Clock clock;

    public FineService(
            FineRepository fineRepository,
            UserService userService
    ) {
        this.fineRepository = fineRepository;
        this.userService = userService;
        this.clock = Clock.systemUTC();
    }

    public List<FineResponse> findMyFines(String email) {
        User user = userService.requireByEmail(email);

        return fineRepository
                .findByLoanUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<FineResponse> findByStatus(FineStatus status) {
        return fineRepository
                .findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public FineResponse markPaid(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fine not found with ID: " + fineId
                ));

        if (fine.isPaid()) {
            throw new BusinessRuleException(
                    "This fine has already been paid"
            );
        }

        fine.markPaid(LocalDateTime.now(clock));

        return toResponse(fine);
    }

    private FineResponse toResponse(Fine fine) {
        return new FineResponse(
                fine.getId(),
                fine.getLoan().getId(),
                fine.getLoan().getUser().getId(),
                fine.getLoan().getUser().getEmail(),
                fine.getLoan().getBook().getTitle(),
                fine.getAmount(),
                fine.getOverdueDays(),
                fine.getStatus().name(),
                fine.getCreatedAt(),
                fine.getPaidAt()
        );
    }
}