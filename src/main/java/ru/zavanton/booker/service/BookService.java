package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.BookEntity;
import ru.zavanton.booker.repository.BookRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.BookEntity}.
 */
@Service
@Transactional
public class BookService {

    private static final Logger LOG = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Save a book.
     *
     * @param bookEntity the entity to save.
     * @return the persisted entity.
     */
    public BookEntity save(BookEntity bookEntity) {
        LOG.debug("Request to save Book : {}", bookEntity);
        return bookRepository.save(bookEntity);
    }

    /**
     * Update a book.
     *
     * @param bookEntity the entity to save.
     * @return the persisted entity.
     */
    public BookEntity update(BookEntity bookEntity) {
        LOG.debug("Request to update Book : {}", bookEntity);
        return bookRepository.save(bookEntity);
    }

    /**
     * Partially update a book.
     *
     * @param bookEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookEntity> partialUpdate(BookEntity bookEntity) {
        LOG.debug("Request to partially update Book : {}", bookEntity);

        return bookRepository
            .findById(bookEntity.getId())
            .map(existingBook -> {
                if (bookEntity.getIsbn() != null) {
                    existingBook.setIsbn(bookEntity.getIsbn());
                }
                if (bookEntity.getTitle() != null) {
                    existingBook.setTitle(bookEntity.getTitle());
                }
                if (bookEntity.getDescription() != null) {
                    existingBook.setDescription(bookEntity.getDescription());
                }
                if (bookEntity.getCoverImageUrl() != null) {
                    existingBook.setCoverImageUrl(bookEntity.getCoverImageUrl());
                }
                if (bookEntity.getPageCount() != null) {
                    existingBook.setPageCount(bookEntity.getPageCount());
                }
                if (bookEntity.getPublicationDate() != null) {
                    existingBook.setPublicationDate(bookEntity.getPublicationDate());
                }
                if (bookEntity.getLanguage() != null) {
                    existingBook.setLanguage(bookEntity.getLanguage());
                }
                if (bookEntity.getAverageRating() != null) {
                    existingBook.setAverageRating(bookEntity.getAverageRating());
                }
                if (bookEntity.getTotalRatings() != null) {
                    existingBook.setTotalRatings(bookEntity.getTotalRatings());
                }
                if (bookEntity.getTotalReviews() != null) {
                    existingBook.setTotalReviews(bookEntity.getTotalReviews());
                }
                if (bookEntity.getCreatedAt() != null) {
                    existingBook.setCreatedAt(bookEntity.getCreatedAt());
                }
                if (bookEntity.getUpdatedAt() != null) {
                    existingBook.setUpdatedAt(bookEntity.getUpdatedAt());
                }

                return existingBook;
            })
            .map(bookRepository::save);
    }

    /**
     * Get one book by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookEntity> findOne(Long id) {
        LOG.debug("Request to get Book : {}", id);
        return bookRepository.findById(id);
    }

    /**
     * Delete the book by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Book : {}", id);
        bookRepository.deleteById(id);
    }
}
