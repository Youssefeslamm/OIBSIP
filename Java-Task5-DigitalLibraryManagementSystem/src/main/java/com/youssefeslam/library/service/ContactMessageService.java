package com.youssefeslam.library.service;

import com.youssefeslam.library.dto.message.ContactMessageRequest;
import com.youssefeslam.library.dto.message.ContactMessageResponse;
import com.youssefeslam.library.entity.ContactMessage;
import com.youssefeslam.library.entity.MessageStatus;
import com.youssefeslam.library.entity.User;
import com.youssefeslam.library.exception.BusinessRuleException;
import com.youssefeslam.library.exception.ResourceNotFoundException;
import com.youssefeslam.library.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContactMessageService {

    private final ContactMessageRepository messageRepository;
    private final UserService userService;
    private final Clock clock;

    public ContactMessageService(
            ContactMessageRepository messageRepository,
            UserService userService
    ) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.clock = Clock.systemUTC();
    }

    @Transactional
    public ContactMessageResponse create(
            String email,
            ContactMessageRequest request
    ) {
        User user = userService.requireByEmail(email);

        ContactMessage message = new ContactMessage(
                user,
                request.subject(),
                request.message()
        );

        return toResponse(
                messageRepository.save(message)
        );
    }

    public List<ContactMessageResponse> findMine(
            String email
    ) {
        User user = userService.requireByEmail(email);

        return messageRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ContactMessageResponse> findByStatus(
            MessageStatus status
    ) {
        return messageRepository
                .findByStatusOrderByCreatedAtAsc(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ContactMessageResponse resolve(Long messageId) {
        ContactMessage message = messageRepository
                .findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contact message not found with ID: "
                                + messageId
                ));

        if (message.getStatus() == MessageStatus.RESOLVED) {
            throw new BusinessRuleException(
                    "This message has already been resolved"
            );
        }

        message.resolve(LocalDateTime.now(clock));

        return toResponse(message);
    }

    private ContactMessageResponse toResponse(
            ContactMessage message
    ) {
        return new ContactMessageResponse(
                message.getId(),
                message.getUser().getId(),
                message.getUser().getEmail(),
                message.getUser().getDisplayName(),
                message.getSubject(),
                message.getMessage(),
                message.getStatus().name(),
                message.getCreatedAt(),
                message.getResolvedAt()
        );
    }
}