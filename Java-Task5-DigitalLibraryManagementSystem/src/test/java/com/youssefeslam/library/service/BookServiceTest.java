package com.youssefeslam.library.service;

import com.youssefeslam.library.dto.book.BookRequest;
import com.youssefeslam.library.dto.book.BookResponse;
import com.youssefeslam.library.entity.Book;
import com.youssefeslam.library.entity.Category;
import com.youssefeslam.library.exception.DuplicateResourceException;
import com.youssefeslam.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryService categoryService;

    private BookService bookService;
    private Category category;

    @BeforeEach
    void setUp() {
        bookService = new BookService(
                bookRepository,
                categoryService
        );

        category = new Category(
                "Software Engineering",
                "Programming and software design"
        );
    }

    @Test
    void createsBookWithNormalizedIsbn() {
        BookRequest request = new BookRequest(
                "Effective Java",
                "Joshua Bloch",
                "978-0-13-468599-1",
                1L,
                3
        );

        when(bookRepository.existsByIsbn("9780134685991"))
                .thenReturn(false);
        when(categoryService.requireCategory(1L))
                .thenReturn(category);
        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse response = bookService.create(request);

        ArgumentCaptor<Book> captor =
                ArgumentCaptor.forClass(Book.class);

        verify(bookRepository).save(captor.capture());

        Book savedBook = captor.getValue();

        assertEquals("9780134685991", savedBook.getIsbn());
        assertEquals(3, savedBook.getTotalQuantity());
        assertEquals(3, savedBook.getAvailableQuantity());
        assertEquals("Effective Java", response.title());
        assertTrue(response.available());
    }

    @Test
    void rejectsDuplicateIsbn() {
        BookRequest request = new BookRequest(
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                1L,
                3
        );

        when(bookRepository.existsByIsbn("9780134685991"))
                .thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> bookService.create(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));

        verify(bookRepository, never()).save(any());
        verifyNoInteractions(categoryService);
    }

    @Test
    void archivesExistingBook() {
        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                category,
                2
        );

        when(bookRepository.findById(10L))
                .thenReturn(Optional.of(book));

        BookResponse response = bookService.archive(10L);

        assertTrue(book.isArchived());
        assertTrue(response.archived());
        assertFalse(response.available());
    }

    @Test
    void preventsReducingQuantityBelowIssuedCopies() {
        Book book = new Book(
                "Clean Architecture",
                "Robert C. Martin",
                "9780134494166",
                category,
                3
        );

        book.issueCopy();
        book.issueCopy();

        BookRequest request = new BookRequest(
                "Clean Architecture",
                "Robert C. Martin",
                "9780134494166",
                1L,
                1
        );

        when(bookRepository.findById(20L))
                .thenReturn(Optional.of(book));
        when(bookRepository.existsByIsbnAndIdNot(
                "9780134494166",
                20L
        )).thenReturn(false);
        when(categoryService.requireCategory(1L))
                .thenReturn(category);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.update(20L, request)
        );

        assertEquals(
                "Total quantity cannot be less than issued copies",
                exception.getMessage()
        );
    }
}