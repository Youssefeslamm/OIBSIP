package com.youssefeslam.library.service;

import com.youssefeslam.library.dto.book.BookRequest;
import com.youssefeslam.library.dto.book.BookResponse;
import com.youssefeslam.library.entity.Book;
import com.youssefeslam.library.entity.Category;
import com.youssefeslam.library.exception.DuplicateResourceException;
import com.youssefeslam.library.exception.ResourceNotFoundException;
import com.youssefeslam.library.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryService categoryService;

    public BookService(
            BookRepository bookRepository,
            CategoryService categoryService
    ) {
        this.bookRepository = bookRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        String isbn = normalizeIsbn(request.isbn());

        if (bookRepository.existsByIsbn(isbn)) {
            throw new DuplicateResourceException(
                    "A book with ISBN " + isbn + " already exists"
            );
        }

        Category category =
                categoryService.requireCategory(request.categoryId());

        Book book = new Book(
                normalizeRequiredText(request.title(), "Title"),
                normalizeRequiredText(request.author(), "Author"),
                isbn,
                category,
                request.totalQuantity()
        );

        return toResponse(bookRepository.save(book));
    }

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = requireBook(id);
        String isbn = normalizeIsbn(request.isbn());

        if (bookRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new DuplicateResourceException(
                    "Another book already uses ISBN " + isbn
            );
        }

        Category category =
                categoryService.requireCategory(request.categoryId());

        book.changeDetails(
                normalizeRequiredText(request.title(), "Title"),
                normalizeRequiredText(request.author(), "Author"),
                isbn,
                category
        );

        book.changeTotalQuantity(request.totalQuantity());

        return toResponse(book);
    }

    @Transactional
    public BookResponse archive(Long id) {
        Book book = requireBook(id);
        book.archive();
        return toResponse(book);
    }

    @Transactional
    public BookResponse restore(Long id) {
        Book book = requireBook(id);
        book.restore();
        return toResponse(book);
    }

    public BookResponse findById(Long id) {
        return toResponse(requireBook(id));
    }

    public Page<BookResponse> findActive(Pageable pageable) {
        return bookRepository.findByArchivedFalse(pageable)
                .map(this::toResponse);
    }

    public Page<BookResponse> findByCategory(
            Long categoryId,
            Pageable pageable
    ) {
        categoryService.requireCategory(categoryId);

        return bookRepository
                .findByCategoryIdAndArchivedFalse(
                        categoryId,
                        pageable
                )
                .map(this::toResponse);
    }

    public Page<BookResponse> search(
            String query,
            Pageable pageable
    ) {
        if (query == null || query.isBlank()) {
            return findActive(pageable);
        }

        return bookRepository
                .searchActiveBooks(query.trim(), pageable)
                .map(this::toResponse);
    }

    public Book requireBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with ID: " + id
                ));
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCategory().getId(),
                book.getCategory().getName(),
                book.getTotalQuantity(),
                book.getAvailableQuantity(),
                book.isAvailable(),
                book.isArchived()
        );
    }

    private String normalizeIsbn(String isbn) {
        return normalizeRequiredText(isbn, "ISBN")
                .replace("-", "")
                .replace(" ", "");
    }

    private String normalizeRequiredText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return value.trim();
    }
}