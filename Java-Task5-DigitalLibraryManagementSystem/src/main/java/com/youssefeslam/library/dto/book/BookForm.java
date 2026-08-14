package com.youssefeslam.library.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookForm {

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 150)
    private String author;

    @NotBlank(message = "ISBN is required")
    @Size(max = 20)
    private String isbn;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer totalQuantity;

    public BookRequest toRequest() {
        return new BookRequest(
                title,
                author,
                isbn,
                categoryId,
                totalQuantity
        );
    }

    public static BookForm from(BookResponse book) {
        BookForm form = new BookForm();
        form.setTitle(book.title());
        form.setAuthor(book.author());
        form.setIsbn(book.isbn());
        form.setCategoryId(book.categoryId());
        form.setTotalQuantity(book.totalQuantity());
        return form;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}