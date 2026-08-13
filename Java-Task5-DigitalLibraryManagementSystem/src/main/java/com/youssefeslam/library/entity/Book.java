package com.youssefeslam.library.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 150)
    private String author;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(nullable = false)
    private boolean archived;

    @Column(name = "created_at", nullable = false,
            insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false,
            insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    protected Book() {
    }

    public Book(
            String title,
            String author,
            String isbn,
            Category category,
            int totalQuantity
    ) {
        changeDetails(title, author, isbn, category);
        changeTotalQuantity(totalQuantity);
    }

    public void changeDetails(
            String title,
            String author,
            String isbn,
            Category category
    ) {
        this.title = Objects.requireNonNull(title);
        this.author = Objects.requireNonNull(author);
        this.isbn = Objects.requireNonNull(isbn);
        this.category = Objects.requireNonNull(category);
    }

    public void changeTotalQuantity(int newTotalQuantity) {
        if (newTotalQuantity < 0) {
            throw new IllegalArgumentException(
                    "Total quantity cannot be negative"
            );
        }

        int issuedCopies = totalQuantity - availableQuantity;

        if (newTotalQuantity < issuedCopies) {
            throw new IllegalArgumentException(
                    "Total quantity cannot be less than issued copies"
            );
        }

        totalQuantity = newTotalQuantity;
        availableQuantity = newTotalQuantity - issuedCopies;
    }

    public void issueCopy() {
        if (archived) {
            throw new IllegalStateException(
                    "Archived books cannot be issued"
            );
        }

        if (availableQuantity <= 0) {
            throw new IllegalStateException(
                    "No copies are currently available"
            );
        }

        availableQuantity--;
    }

    public void returnCopy() {
        if (availableQuantity >= totalQuantity) {
            throw new IllegalStateException(
                    "All copies are already available"
            );
        }

        availableQuantity++;
    }

    public boolean isAvailable() {
        return !archived && availableQuantity > 0;
    }

    public void archive() {
        archived = true;
    }

    public void restore() {
        archived = false;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public Category getCategory() {
        return category;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public boolean isArchived() {
        return archived;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Book book)) {
            return false;
        }

        return id != null && Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}