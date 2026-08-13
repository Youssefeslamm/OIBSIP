package com.youssefeslam.library.repository;

import com.youssefeslam.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

    Page<Book> findByArchivedFalse(Pageable pageable);

    Page<Book> findByCategoryIdAndArchivedFalse(
            Long categoryId,
            Pageable pageable
    );

    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String title,
            String author,
            Pageable pageable
    );
}