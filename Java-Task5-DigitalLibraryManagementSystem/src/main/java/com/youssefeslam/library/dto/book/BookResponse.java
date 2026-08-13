package com.youssefeslam.library.dto.book;

public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        Long categoryId,
        String categoryName,
        int totalQuantity,
        int availableQuantity,
        boolean available,
        boolean archived
) {
}