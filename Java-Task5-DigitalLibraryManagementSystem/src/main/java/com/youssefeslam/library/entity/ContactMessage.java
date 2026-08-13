package com.youssefeslam.library.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String subject;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageStatus status;

    @Column(name = "created_at", nullable = false,
            insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    protected ContactMessage() {
    }

    public ContactMessage(
            User user,
            String subject,
            String message
    ) {
        this.user = Objects.requireNonNull(user);
        this.subject = requireText(subject, "Subject");
        this.message = requireText(message, "Message");
        this.status = MessageStatus.OPEN;
    }

    public void resolve(LocalDateTime resolutionTime) {
        Objects.requireNonNull(resolutionTime);

        if (status == MessageStatus.RESOLVED) {
            throw new IllegalStateException(
                    "This message is already resolved"
            );
        }

        status = MessageStatus.RESOLVED;
        resolvedAt = resolutionTime;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return value.trim();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof ContactMessage contactMessage)) {
            return false;
        }

        return id != null && Objects.equals(
                id,
                contactMessage.id
        );
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}