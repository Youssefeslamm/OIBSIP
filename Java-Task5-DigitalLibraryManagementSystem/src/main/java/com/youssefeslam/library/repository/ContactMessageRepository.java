package com.youssefeslam.library.repository;

import com.youssefeslam.library.entity.ContactMessage;
import com.youssefeslam.library.entity.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMessageRepository
        extends JpaRepository<ContactMessage, Long> {

    List<ContactMessage> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    List<ContactMessage> findByStatusOrderByCreatedAtAsc(
            MessageStatus status
    );
}