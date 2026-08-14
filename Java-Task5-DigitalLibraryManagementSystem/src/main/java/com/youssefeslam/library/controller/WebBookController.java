package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.book.BookResponse;
import com.youssefeslam.library.dto.category.CategoryResponse;
import com.youssefeslam.library.service.BookService;
import com.youssefeslam.library.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebBookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    public WebBookController(
            BookService bookService,
            CategoryService categoryService
    ) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @GetMapping("/books")
    public String catalogue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            Model model
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(
                Math.max(size, 1),
                50
        );

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize
        );

        Page<BookResponse> books;

        if (query != null && !query.isBlank()) {
            books = bookService.search(query, pageable);
        } else if (categoryId != null) {
            books = bookService.findByCategory(
                    categoryId,
                    pageable
            );
        } else {
            books = bookService.findActive(pageable);
        }

        List<CategoryResponse> categories =
                categoryService.findAll();

        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        model.addAttribute("query", query);
        model.addAttribute("selectedCategoryId", categoryId);

        return "books/catalogue";
    }
}