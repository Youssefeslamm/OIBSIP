package com.youssefeslam.library.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @NotBlank(message = "Author is required")
        @Size(max = 150, message = "Author cannot exceed 150 characters")
        String author,

        @NotBlank(message = "ISBN is required")
        @Size(max = 20, message = "ISBN cannot exceed 20 characters")
        String isbn,

        @NotNull(message = "Category is required")
        Long categoryId,

        @NotNull(message = "Total quantity is required")
        @Min(value = 0, message = "Total quantity cannot be negative")
        Integer totalQuantity
) {
}