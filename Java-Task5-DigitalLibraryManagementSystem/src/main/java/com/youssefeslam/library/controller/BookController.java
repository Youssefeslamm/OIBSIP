package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.book.BookRequest;
import com.youssefeslam.library.dto.book.BookResponse;
import com.youssefeslam.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(
            @Valid @RequestBody BookRequest request
    ) {
        return bookService.create(request);
    }

    @PutMapping("/{id}")
    public BookResponse update(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request
    ) {
        return bookService.update(id, request);
    }

    @PatchMapping("/{id}/archive")
    public BookResponse archive(@PathVariable Long id) {
        return bookService.archive(id);
    }

    @PatchMapping("/{id}/restore")
    public BookResponse restore(@PathVariable Long id) {
        return bookService.restore(id);
    }

    @GetMapping("/{id}")
    public BookResponse findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @GetMapping
    public Page<BookResponse> findActive(
            @PageableDefault(size = 12, sort = "title")
            Pageable pageable
    ) {
        return bookService.findActive(pageable);
    }

    @GetMapping("/category/{categoryId}")
    public Page<BookResponse> findByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 12, sort = "title")
            Pageable pageable
    ) {
        return bookService.findByCategory(categoryId, pageable);
    }

    @GetMapping("/search")
    public Page<BookResponse> search(
            @RequestParam String query,
            @PageableDefault(size = 12, sort = "title")
            Pageable pageable
    ) {
        return bookService.search(query, pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}