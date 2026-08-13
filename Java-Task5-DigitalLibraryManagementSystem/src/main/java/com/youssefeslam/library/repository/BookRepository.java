package com.youssefeslam.library.repository;

import com.youssefeslam.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    Optional<Book> findByIsbn(String isbn);

    Page<Book> findByArchivedFalse(Pageable pageable);

    Page<Book> findByCategoryIdAndArchivedFalse(
            Long categoryId,
            Pageable pageable
    );

    @Query("""
            SELECT book
            FROM Book book
            WHERE book.archived = false
              AND (
                    LOWER(book.title) LIKE LOWER(CONCAT('%', :query, '%'))
                    OR LOWER(book.author) LIKE LOWER(CONCAT('%', :query, '%'))
                  )
            """)
    Page<Book> searchActiveBooks(
            @Param("query") String query,
            Pageable pageable
    );
}