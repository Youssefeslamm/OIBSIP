package com.youssefeslam.library.controller;

import com.youssefeslam.library.dto.book.BookForm;
import com.youssefeslam.library.dto.book.BookResponse;
import com.youssefeslam.library.exception.BusinessRuleException;
import com.youssefeslam.library.exception.DuplicateResourceException;
import com.youssefeslam.library.exception.ResourceNotFoundException;
import com.youssefeslam.library.service.BookService;
import com.youssefeslam.library.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/books")
public class AdminBookWebController {

    private final BookService bookService;
    private final CategoryService categoryService;

    public AdminBookWebController(
            BookService bookService,
            CategoryService categoryService
    ) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<BookResponse> books = bookService.findAll(
                PageRequest.of(Math.max(page, 0), 20)
        );

        model.addAttribute("books", books);

        return "admin/books/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        prepareForm(
                model,
                new BookForm(),
                "Add book",
                "/admin/books"
        );

        return "admin/books/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("bookForm")
            BookForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareForm(
                    model,
                    form,
                    "Add book",
                    "/admin/books"
            );

            return "admin/books/form";
        }

        try {
            bookService.create(form.toRequest());

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Book added successfully"
            );

            return "redirect:/admin/books";
        } catch (
                DuplicateResourceException
                | ResourceNotFoundException
                | IllegalArgumentException exception
        ) {
            bindingResult.reject(
                    "book.error",
                    exception.getMessage()
            );

            prepareForm(
                    model,
                    form,
                    "Add book",
                    "/admin/books"
            );

            return "admin/books/form";
        }
    }

    @GetMapping("/{bookId}/edit")
    public String editForm(
            @PathVariable Long bookId,
            Model model
    ) {
        BookResponse book = bookService.findById(bookId);

        prepareForm(
                model,
                BookForm.from(book),
                "Edit book",
                "/admin/books/" + bookId
        );

        return "admin/books/form";
    }

    @PostMapping("/{bookId}")
    public String update(
            @PathVariable Long bookId,
            @Valid @ModelAttribute("bookForm")
            BookForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareForm(
                    model,
                    form,
                    "Edit book",
                    "/admin/books/" + bookId
            );

            return "admin/books/form";
        }

        try {
            bookService.update(bookId, form.toRequest());

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Book updated successfully"
            );

            return "redirect:/admin/books";
        } catch (
                DuplicateResourceException
                | ResourceNotFoundException
                | IllegalArgumentException exception
        ) {
            bindingResult.reject(
                    "book.error",
                    exception.getMessage()
            );

            prepareForm(
                    model,
                    form,
                    "Edit book",
                    "/admin/books/" + bookId
            );

            return "admin/books/form";
        }
    }

    @PostMapping("/{bookId}/archive")
    public String archive(
            @PathVariable Long bookId,
            RedirectAttributes redirectAttributes
    ) {
        bookService.archive(bookId);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Book archived"
        );

        return "redirect:/admin/books";
    }

    @PostMapping("/{bookId}/restore")
    public String restore(
            @PathVariable Long bookId,
            RedirectAttributes redirectAttributes
    ) {
        bookService.restore(bookId);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Book restored"
        );

        return "redirect:/admin/books";
    }

    private void prepareForm(
            Model model,
            BookForm form,
            String title,
            String action
    ) {
        model.addAttribute("bookForm", form);
        model.addAttribute(
                "categories",
                categoryService.findAll()
        );
        model.addAttribute("formTitle", title);
        model.addAttribute("formAction", action);
    }

    @PostMapping("/{bookId}/delete")
    public String delete(
            @PathVariable Long bookId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            bookService.delete(bookId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Book permanently deleted"
            );
        } catch (
                BusinessRuleException
                | ResourceNotFoundException exception
        ) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/admin/books";
    }
}